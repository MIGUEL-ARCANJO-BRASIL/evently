package fametro.edu.br.evently.event.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventSubscriptionResponseDTO {
    private UUID id;
}
