package fametro.edu.br.evently.event.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter@NoArgsConstructor
public class CategoryResponseDTO {
    UUID id;
    String name;
}
