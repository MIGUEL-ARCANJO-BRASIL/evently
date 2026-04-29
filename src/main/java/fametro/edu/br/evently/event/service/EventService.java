package fametro.edu.br.evently.event.service;

import fametro.edu.br.evently.event.dto.EventFormDTO;
import fametro.edu.br.evently.event.dto.EventTicketDTO;
import fametro.edu.br.evently.event.enums.EventStatus;
import fametro.edu.br.evently.event.model.Category;
import fametro.edu.br.evently.event.model.Event;
import fametro.edu.br.evently.event.model.EventLocalization;
import fametro.edu.br.evently.event.model.EventTicket;
import fametro.edu.br.evently.event.repository.CategoryRepository;
import fametro.edu.br.evently.event.repository.EventRepository;
import fametro.edu.br.evently.event.repository.EventTicketRepository;
import fametro.edu.br.evently.user.model.User;
import fametro.edu.br.evently.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventService {

    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final EventTicketRepository eventTicketRepository;

    public List<Event> findFiltered(String category, String query, String date, String price, String city) {
        String categoryParam = (category != null && !category.trim().isEmpty()) ? category : null;
        String queryParam = (query != null && !query.trim().isEmpty()) ? query : null;
        
        List<Event> events = eventRepository.findFilteredEvents(EventStatus.ATIVO, categoryParam, queryParam);

        // Filter by City
        if (city != null && !city.trim().isEmpty()) {
            events = events.stream()
                    .filter(e -> e.getEventLocalization() != null && 
                            e.getEventLocalization().getCity() != null &&
                            e.getEventLocalization().getCity().equalsIgnoreCase(city.trim()))
                    .toList();
        }
        
        // Filter by Date
        if (date != null && !date.isEmpty()) {
            LocalDateTime now = LocalDateTime.now();
            events = events.stream().filter(e -> {
                LocalDate eventLocalDate = e.getEventDate().toLocalDate();
                if (date.equals("hoje")) {
                    return eventLocalDate.isEqual(now.toLocalDate());
                } else if (date.equals("amanha")) {
                    return eventLocalDate.isEqual(now.plusDays(1).toLocalDate());
                } else if (date.equals("fim-de-semana")) {
                    LocalDate saturday = now.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY)).toLocalDate();
                    LocalDate sunday = saturday.plusDays(1);
                    return eventLocalDate.isEqual(saturday) || eventLocalDate.isEqual(sunday);
                } else {
                    try {
                        // Tenta parsear como data específica (YYYY-MM-DD do input date)
                        return eventLocalDate.isEqual(LocalDate.parse(date));
                    } catch (Exception ex) {
                        return true;
                    }
                }
            }).toList();
        }
        
        // Filter by Price
        if (price != null && !price.isEmpty()) {
            events = events.stream().filter(e -> {
                boolean hasFree = e.getTickets().stream().anyMatch(t -> t.getValue() == 0);
                boolean hasPaid = e.getTickets().stream().anyMatch(t -> t.getValue() > 0);
                
                if (price.equals("gratis")) return hasFree;
                if (price.equals("pago")) return hasPaid;
                return true;
            }).toList();
        }
        
        return events;
    }

    public List<Event> findAllByOrganizer(UUID organizerId) {
        return eventRepository.findAllByOrganizer_IdOrderByEventStatusDesc(organizerId);

    }

    public List<Event> findFilteredByOrganizer(UUID organizerId, String category, String query) {
        String categoryParam = (category != null && !category.trim().isEmpty()) ? category : null;
        String queryParam = (query != null && !query.trim().isEmpty()) ? query : null;
        return eventRepository.findFilteredEventsByOrganizer(organizerId, categoryParam, queryParam);
    }

    public Event findById(UUID id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evento não encontrado"));
    }

    @Transactional
    public Event create(EventFormDTO form, User organizer) {
        log.info("Criando evento...");

        String imageName = saveImage(form.getCoverImage());

        Category category = null;
        if (form.getCategoryId() != null) {
            category = categoryRepository.findById(form.getCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada"));
        }

        var eventLocalization = EventLocalization.builder()
                .cep(form.getCep())
                .address(form.getAddress())
                .complement(form.getComplement())
                .number(form.getNumber())
                .city(form.getCity())
                .state(form.getState())
                .neighborhood(form.getNeighborhood())
                .build();

        Event event = Event.builder()
                .title(form.getTitle())
                .description(form.getDescription())
                .eventDate(form.getEventDate())
                .eventLocalization(eventLocalization)
                .coverImage(imageName)
                .category(category)
                .organizer(organizer)
                .eventStatus(EventStatus.ATIVO)
                .registrationDeadline(form.getRegistrationDeadline())
                .totalSlots(form.getTotalSlots())
                .availableSlots(form.getTotalSlots())
                .build();

        Event savedEvent = eventRepository.save(event);

        if (form.getEventTicket() != null && !form.getEventTicket().isEmpty()) {
            List<EventTicket> tickets = createTickets(form.getEventTicket(), savedEvent);
            savedEvent.setTickets(tickets);
        }

        log.info("Evento '{}' criado por {}", savedEvent.getTitle(), organizer.getEmail());
        return savedEvent;
    }

    private List<EventTicket> createTickets(List<EventTicketDTO> dtos, Event event) {
        List<EventTicket> tickets = dtos.stream().map(t ->
                EventTicket.builder()
                        .name(t.getName())
                        .expirationDate(t.getExpirationDate())
                        .value(t.getValue())
                        .quantity(t.getQuantity())
                        .event(event)
                        .build()
        ).toList();

        return eventTicketRepository.saveAll(tickets);
    }
    @Transactional
    public Event update(UUID id, EventFormDTO form) {
        log.info("Atualizando evento ID: {}", id);

        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Evento não encontrado"));

        if (form.getCoverImage() != null && !form.getCoverImage().isEmpty()) {
            event.setCoverImage(saveImage(form.getCoverImage()));
        }

        if (form.getCategoryId() != null) {
            Category category = categoryRepository.findById(form.getCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada"));
            event.setCategory(category);
        }

        var eventLocalization = EventLocalization.builder()
                .cep(form.getCep())
                .address(form.getAddress())
                .complement(form.getComplement())
                .number(form.getNumber())
                .city(form.getCity())
                .state(form.getState())
                .neighborhood(form.getNeighborhood())
                .build();
        event.setEventLocalization(eventLocalization);

        event.setTitle(form.getTitle());
        event.setDescription(form.getDescription());
        event.setEventDate(form.getEventDate());
        event.setRegistrationDeadline(form.getRegistrationDeadline());

        if (form.getEventTicket() != null) {
            event.getTickets().clear();

            List<EventTicket> newTickets = form.getEventTicket().stream()
                    .map(t -> EventTicket.builder()
                            .name(t.getName())
                            .value(t.getValue())
                            .quantity(t.getQuantity())
                            .expirationDate(t.getExpirationDate())
                            .event(event)
                            .build())
                    .toList();

            event.getTickets().addAll(newTickets);
        }

        log.info("Evento '{}' e seus tickets foram atualizados", event.getTitle());
        return eventRepository.save(event);
    }

    public void archiveOrUnarchive(UUID id, EventStatus eventStatus) {
        Event event = findById(id);
        if (eventStatus == event.getEventStatus())
            return;
        event.setEventStatus(eventStatus);
        eventRepository.save(event);
        log.info("Status do evento '{}' alterado", event.getTitle());
    }

    public void unarchive(UUID id) {
        Event event = findById(id);
        event.setEventStatus(EventStatus.ATIVO);
        eventRepository.save(event);
        log.info("Evento '{}' desativado", event.getTitle());
    }

    private String saveImage(MultipartFile file) {
        log.info("saveImage chamado. file={}, isEmpty={}", file, file != null ? file.isEmpty() : "null");
        if (file == null || file.isEmpty())
            return null;
        try {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path destination = Paths.get("uploads/" + fileName).toAbsolutePath(); // ← toAbsolutePath para ver o caminho
            // exato
            log.info("Salvando imagem em: {}", destination);
            Files.createDirectories(destination.getParent());
            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
            log.info("Imagem salva com sucesso: {}", fileName);
            return fileName;
        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar imagem", e);
        }
    }
}