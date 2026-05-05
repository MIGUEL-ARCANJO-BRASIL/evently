package fametro.edu.br.evently.event.dto;

import fametro.edu.br.evently.event.enums.PaymentMethod;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventSubscriptionSummaryDTO {
    private String eventTitle;
    private LocalDateTime eventDate;
    private String eventCity;
    private String eventState;
    private String userName;
    private List<String> ticketsName;
    private UUID idSubscription;
    private UUID idEventTransaction;
    private PaymentMethod paymentMethod;
    private LocalDateTime purchaseDate;
    private String userEmail;

}
