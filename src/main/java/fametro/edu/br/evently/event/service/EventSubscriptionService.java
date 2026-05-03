package fametro.edu.br.evently.event.service;

import fametro.edu.br.evently.event.dto.JoinEventFormDTO;
import fametro.edu.br.evently.event.model.EventSubscription;
import fametro.edu.br.evently.event.repository.EventRepository;
import fametro.edu.br.evently.event.repository.EventSubscriptionRepository;
import fametro.edu.br.evently.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventSubscriptionService {
    private final EventSubscriptionRepository subscriptionRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public void joinEvent(JoinEventFormDTO dto) {
        var event = this.eventRepository.findById(dto.getEventId())
                .orElseThrow(() -> new EntityNotFoundException("Evento não encontrado"));

        validateUser(dto.getEventId(), dto);

        double totalPaid = calculatePaidValue(event, dto.getSelectedTickets());

        var eventSubscription = EventSubscription
                .builder()
                .event(event)
                .userEmail(dto.getUserEmail())
                .userName(dto.getUserName())
                .userSecondName(dto.getUserSecondName())
                .userCpf(dto.getUserCpf())
                .userPhone(dto.getUserPhone())
                .userSecondPhone(dto.getUserSecondPhone())
                .userCity(dto.getUserCity())
                .paymentMethod(dto.getPaymentMethod())
                .paidValue(totalPaid)
                .subscriptionDate(LocalDateTime.now())
                .userAgeRange(dto.getUserAgeRange())
                .build();

        this.subscriptionRepository.save(eventSubscription);
    }

    private void validateUser(UUID eventId, JoinEventFormDTO dto) {

        this.subscriptionRepository.findByEvent_IdAndUserEmail(eventId, dto.getUserEmail()).ifPresent(e -> {
            throw new RuntimeException("Já existe uma inscrição para este email neste evento.");
        });

        this.userRepository.findByEmail(dto.getUserEmail()).ifPresent(e -> {
            if (!e.getCpf().equals(dto.getUserCpf())) {
                throw new RuntimeException("O email informado já está associado a um CPF diferente.");
            }
        });
        this.userRepository.findByCpf(dto.getUserCpf()).ifPresent(e -> {
            if (!e.getEmail().equals(dto.getUserEmail())) {
                throw new RuntimeException("O CPF informado já está associado a um email diferente.");
            }
        });

        this.subscriptionRepository.findByEvent_IdAndUserCpf(eventId, dto.getUserEmail()).ifPresent(e -> {
            throw new RuntimeException("Já existe uma inscrição para este CPF neste evento.");
        });

    }

    private double calculatePaidValue(fametro.edu.br.evently.event.model.Event event, String selectedTickets) {
        if (selectedTickets == null || selectedTickets.isBlank()) {
            return 0.0;
        }

        Map<UUID, Double> priceByTicketId = event.getTickets().stream()
                .collect(Collectors.toMap(
                        fametro.edu.br.evently.event.model.EventTicket::getId,
                        ticket -> ticket.getValue() != null ? ticket.getValue() : 0.0));

        return Arrays.stream(selectedTickets.split(","))
                .map(String::trim)
                .filter(part -> !part.isBlank() && part.contains(":"))
                .map(part -> part.split(":"))
                .filter(parts -> parts.length == 2)
                .map(parts -> Map.entry(parseUuid(parts[0]), parseQuantity(parts[1])))
                .filter(entry -> entry.getKey() != null && entry.getValue() > 0)
                .mapToDouble(entry -> priceByTicketId.getOrDefault(entry.getKey(), 0.0) * entry.getValue())
                .sum();
    }

    private UUID parseUuid(String value) {
        try {
            return UUID.fromString(value.trim());
        } catch (Exception ignored) {
            return null;
        }
    }

    private int parseQuantity(String value) {
        try {
            return Integer.parseInt(value.trim());
        } catch (Exception ignored) {
            return 0;
        }
    }

}
