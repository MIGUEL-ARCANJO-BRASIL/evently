package fametro.edu.br.evently.user.dto;

import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.br.CPF;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record UserEditInfosForm(
        @NotBlank(message = "Nome é obrigatório")
        @Size(min = 3, max = 120, message = "Nome deve ter entre 3 e 120 caracteres")
        String name,

        @NotBlank(message = "E-mail é obrigatório")
        @Email(message = "E-mail inválido")
        @Size(max = 120, message = "E-mail deve ter no máximo 120 caracteres")
        String email,

        @NotBlank(message = "Telefone é obrigatório")
        @Pattern(
                regexp = "^\\(?\\d{2}\\)?\\s?\\d{4,5}-?\\d{4}$",
                message = "Telefone inválido"
        )
        String phone,

        @NotBlank(message = "CPF é obrigatório")
        @CPF(message = "CPF inválido")
        String cpf,

        @DateTimeFormat(pattern = "yyyy-MM-dd")
        @NotNull(message = "Data de nascimento é obrigatória")
        @Past(message = "Data de nascimento deve estar no passado")
        LocalDate birthDate
) {
}