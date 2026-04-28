package fametro.edu.br.evently.event.model;

import fametro.edu.br.evently.event.enums.SubscriptionStatus;
import fametro.edu.br.evently.user.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tb_event_subscription")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "subscription_date", nullable = false)
    private LocalDateTime subscriptionDate;

    @Enumerated(EnumType.STRING)
    private SubscriptionStatus status;

    private Double paidValue;

    private Boolean checkedIn;
    private LocalDateTime checkInDate;
}
