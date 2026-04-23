package fametro.edu.br.evently.event.dto;

import fametro.edu.br.evently.event.enums.EventStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @NotNull(message = "Data e hora são obrigatórias")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime eventDate;

    @NotBlank(message = "Local é obrigatório")
    private String location;

    @NotNull(message = "Vagas são obrigatórias")
    @Min(value = 1, message = "Mínimo de 1 vaga")
    private Integer totalSlots;

    private UUID categoryId;

    private EventStatus eventStatus = EventStatus.ATIVO;

    private MultipartFile coverImage;
}