package fametro.edu.br.evently.event.model;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor@Builder
public class PaymentCard {

    private String lastFourDigits;

    private String cardBrand;

    private String paymentToken;

}