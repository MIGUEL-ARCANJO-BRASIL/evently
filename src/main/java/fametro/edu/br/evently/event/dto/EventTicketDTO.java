package fametro.edu.br.evently.event.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventTicketDTO {
    private UUID eventId;
    private String name;
    private Double value;
    private LocalDate expirationDate;
    private Integer quantity;
}
