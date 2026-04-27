package fametro.edu.br.evently.event.service;

import fametro.edu.br.evently.event.dto.EventFormDTO;
import fametro.edu.br.evently.event.enums.EventStatus;
import fametro.edu.br.evently.event.model.Category;
import fametro.edu.br.evently.event.model.Event;
import fametro.edu.br.evently.event.repository.CategoryRepository;
import fametro.edu.br.evently.event.repository.EventRepository;
import fametro.edu.br.evently.user.model.User;
import fametro.edu.br.evently.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventService {

    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;


    public List<Event> findFiltered(String category, String query) {
        String categoryParam = (category != null && !category.trim().isEmpty()) ? category : null;
        String queryParam = (query != null && !query.trim().isEmpty()) ? query : null;
        return eventRepository.findFilteredEvents(EventStatus.ATIVO, categoryParam, queryParam);
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

    public Event create(EventFormDTO form, User organizer) {
        log.info("Criando evento...");
        String imageName = saveImage(form.getCoverImage());
        this.userRepository.findByEmail(organizer.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("Organizador não encontrado"));
        Category category = form.getCategoryId() != null
                ? categoryRepository.findById(form.getCategoryId()).orElse(null)
                : null;

        Event event = Event.builder()
                .title(form.getTitle())
                .description(form.getDescription())
                .eventDate(form.getEventDate())
                .location(form.getLocation())
                .totalSlots(form.getTotalSlots())
                .availableSlots(form.getTotalSlots())
                .coverImage(imageName)
                .category(category)
                .organizer(organizer)
                .eventStatus(EventStatus.ATIVO)
                .value(form.getValue())
                .registrationDeadline(form.getRegistrationDeadline())
                .build();

        log.info("Evento '{}' criado por {}", event.getTitle(), organizer.getEmail());
        return eventRepository.save(event);
    }

    @Transactional
    public Event update(UUID id, EventFormDTO form) {
        log.info("Dados recebidos no DTO: Título={}, Data={}", form.getTitle(), form.getEventDate());
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Evento não encontrado"));

        if (form.getCoverImage() != null && !form.getCoverImage().isEmpty()) {
            event.setCoverImage(saveImage(form.getCoverImage()));
        }

        Category category = form.getCategoryId() != null
                ? categoryRepository.findById(form.getCategoryId()).orElse(null)
                : null;

        event.setTitle(form.getTitle());
        event.setDescription(form.getDescription());
        event.setEventDate(form.getEventDate());
        event.setLocation(form.getLocation());
        event.setAvailableSlots(form.getTotalSlots()); // Atualiza as vagas disponíveis para o novo total
        event.setTotalSlots(form.getTotalSlots());
        event.setRegistrationDeadline(form.getRegistrationDeadline());
        event.setValue(form.getValue());
        event.setCategory(category);

        log.info("Evento '{}' atualizado", event.getTitle());
        return eventRepository.save(event);
    }

    public void archiveOrUnarchive(UUID id, EventStatus eventStatus) {
        Event event = findById(id);
        if (eventStatus == event.getEventStatus()) return;
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