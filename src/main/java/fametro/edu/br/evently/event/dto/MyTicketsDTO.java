package fametro.edu.br.evently.event.dto;

import fametro.edu.br.evently.event.model.EventSubscription;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MyTicketsDTO {
    private String eventTitle;
    private LocalDateTime eventDate;
    private String eventCity;
    private String eventState;
    private String ticketName;
    private String userName;
    private UUID idSubscription;
    private UUID eventId;
    private String coverImage;
    private String categoryName;
    private boolean isFinished;
    private Boolean checkedIn;
    private LocalDateTime checkInDate;

    public static MyTicketsDTO toDTO(EventSubscription eventSubscription) {
        var event = eventSubscription.getEvent();
        return MyTicketsDTO
                .builder()
                .eventTitle(event.getTitle())
                .eventDate(event.getEventDate())
                .eventCity(event.getEventLocalization().getCity())
                .eventState(event.getEventLocalization().getState())
                .ticketName(eventSubscription.getItems().stream()
                        .map(item -> item.getTicket().getName() + " (x" + item.getQuantity() + ")")
                        .collect(java.util.stream.Collectors.joining(", ")))
                .userName(eventSubscription.getUserName())
                .idSubscription(eventSubscription.getId())
                .eventId(event.getId())
                .coverImage(event.getCoverImage())
                .categoryName(event.getCategory() != null ? event.getCategory().getName() : "EVENTO")
                .isFinished(event.getEventDate().isBefore(LocalDateTime.now()))
                .checkedIn(eventSubscription.getCheckedIn())
                .checkInDate(eventSubscription.getCheckInDate())
                .build();
    }
}
