package fametro.edu.br.evently.event.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "tb_event_ticket")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventTicket {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;
    private Double value;
    private LocalDate expirationDate;
    private Integer quantity;
    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;
}
