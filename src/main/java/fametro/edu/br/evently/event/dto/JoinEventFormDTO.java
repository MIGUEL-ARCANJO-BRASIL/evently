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

    private PaymentMethod paymentMethod;

    @NotNull(message = "O campo faixa etária não pode ser nulo")
    private AgeRange userAgeRange;

    private String selectedTickets;

    private String lastFourDigits;
    private String cardBrand;
    private String paymentToken;

    public void setEventId(UUID eventId) { this.eventId = eventId; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    public void setUserName(String userName) { this.userName = userName; }
    public void setUserSecondName(String userSecondName) { this.userSecondName = userSecondName; }
    public void setUserCpf(String userCpf) { this.userCpf = userCpf != null ? userCpf.replaceAll("\\D", "") : null; }
    public void setUserPhone(String userPhone) { this.userPhone = userPhone != null ? userPhone.replaceAll("\\D", "") : null; }
    public void setUserSecondPhone(String userSecondPhone) { this.userSecondPhone = userSecondPhone != null ? userSecondPhone.replaceAll("\\D", "") : null; }
    public void setUserCity(String userCity) { this.userCity = userCity; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }
    public void setUserAgeRange(AgeRange userAgeRange) { this.userAgeRange = userAgeRange; }
    public void setSelectedTickets(String selectedTickets) { this.selectedTickets = selectedTickets; }
    public void setLastFourDigits(String lastFourDigits) { this.lastFourDigits = lastFourDigits; }
    public void setCardBrand(String cardBrand) { this.cardBrand = cardBrand; }
    public void setPaymentToken(String paymentToken) { this.paymentToken = paymentToken; }
}
