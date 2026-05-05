package fametro.edu.br.evently.event.model;

import fametro.edu.br.evently.event.enums.PaymentMethod;
import fametro.edu.br.evently.event.enums.TransactionStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tb_event_transaction")
@AllArgsConstructor
@NoArgsConstructor
@Builder@Getter@Setter
public class EventTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // A transação DEVE estar ligada a uma inscrição
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id", nullable = false)
    private EventSubscription subscription;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status;

    private String gatewayTransactionId;

    private LocalDateTime processedAt;
}