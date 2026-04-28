package fametro.edu.br.evently.event.model;

import jakarta.persistence.Embeddable;
import lombok.*;


@Embeddable
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventLocalization {

    private String cep;
    private String address;
    private String city;
    private String state;
    private String complement;
    private String number;
    private String neighborhood;
}
