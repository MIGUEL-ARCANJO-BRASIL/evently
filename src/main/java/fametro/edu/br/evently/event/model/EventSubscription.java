package fametro.edu.br.evently.event.model;

import fametro.edu.br.evently.event.enums.AgeRange;
import fametro.edu.br.evently.event.enums.PaymentMethod;
import fametro.edu.br.evently.event.enums.SubscriptionStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.validator.constraints.br.CPF;

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

    @Column(name = "user_name", nullable = false)
    private String userName;

    @Column(name = "user_second_name", nullable = false)
    private String userSecondName;

    @Column(name = "user_email", nullable = false)
    private String userEmail;

    @Column(name = "user_cpf", nullable = false)
    @CPF
    private String userCpf;

    @Column(name = "user_phone", nullable = false)
    private String userPhone;

    @Column(name = "user_second_phone", nullable = false)
    private String userSecondPhone;

    @Column(name = "user_city", nullable = false)
    private String userCity;

    @Column(name = "user_age_range", nullable = false)
    @Enumerated(EnumType.STRING)
    private AgeRange userAgeRange;

    @Column(name = "cheked_in")
    private Boolean checkedIn;

    @Column(name = "check_in_date")
    private LocalDateTime checkInDate;

    @Column(name = "payment_method")
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Column(name = "paid_value")
    private Double paidValue;

    @Column(name = "subscription_date", nullable = false)
    private LocalDateTime subscriptionDate;

    @Enumerated(EnumType.STRING)
    private SubscriptionStatus status;


}
