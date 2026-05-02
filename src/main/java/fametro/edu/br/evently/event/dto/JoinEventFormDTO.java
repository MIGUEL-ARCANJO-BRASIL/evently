package fametro.edu.br.evently.event.dto;

import fametro.edu.br.evently.event.enums.AgeRange;
import fametro.edu.br.evently.event.enums.PaymentMethod;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.br.CPF;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JoinEventFormDTO {
        @NotNull
        private UUID eventId;

        @Email(message = "O campo email deve ser um endereço de email válido")
        @NotBlank(message = "O campo email não pode estar em branco")
        private String userEmail;

        @NotBlank(message = "O campo nome não pode estar vazio")
        private String userName;

        @NotBlank(message = "O campo sobrenome não pode estar vazio")
        private String userSecondName;

        @CPF(message = "O campo CPF deve ser um CPF válido")
        @NotBlank(message = "O campo CPF não pode estar em branco")
        private String userCpf;

        @NotBlank(message = "O campo telefone não pode estar em branco")
        private String userPhone;

        @NotBlank(message = "O campo telefone de emergência não pode estar em branco")
        private String userSecondPhone;

        @NotBlank(message = "O campo cidade não pode estar em branco")
        private String userCity;

        @NotNull(message = "O campo método de pagamento não pode estar em branco")
        private PaymentMethod paymentMethod;

        @NotNull(message = "O campo faixa etária não pode ser nulo")
        private AgeRange userAgeRange;

        private String selectedTickets;
}
