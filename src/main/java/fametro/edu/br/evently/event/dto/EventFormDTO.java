package fametro.edu.br.evently.event.dto;

import fametro.edu.br.evently.event.enums.EventStatus;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventFormDTO {

    @NotBlank(message = "Título é obrigatório")
    private String title;

    @NotBlank(message = "Descrição é obrigatória")
    private String description;

    @DecimalMin(value = "0.0", message = "Valor deve ser positivo")
    @NotNull(message = "Valor é obrigatório")
    private Double value;

    @NotNull(message = "Data e hora são obrigatórias")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @Future(message = "A data do evento deve ser no futuro")
    private LocalDateTime eventDate;

    @NotNull(message = "Data e hora são obrigatórias")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime registrationDeadline;

    @NotNull(message = "Vagas são obrigatórias")
    @Min(value = 1, message = "Mínimo de 1 vaga")
    private Integer totalSlots;

    private UUID categoryId;

    private EventStatus eventStatus = EventStatus.ATIVO;

    private MultipartFile coverImage;

    private String cep;
    private String address;
    private String complement;
    private String number;
    private String neighborhood;
    private String city;
    private String state;
}