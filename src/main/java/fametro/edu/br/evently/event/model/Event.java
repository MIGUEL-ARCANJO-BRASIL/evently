package fametro.edu.br.evently.event.model;

import fametro.edu.br.evently.event.enums.EventStatus;
import fametro.edu.br.evently.user.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tb_event")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;
    @Column(name = "event_date")
    private LocalDateTime eventDate;

    @Column(name = "registration_deadline")
    private LocalDateTime registrationDeadline;

    private Integer totalSlots;

    private Integer availableSlots;

    private String coverImage;

    @Enumerated(EnumType.STRING)
    private EventStatus eventStatus;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "organizer_id")
    private User organizer;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "cep", column = @Column(name = "loc_cep")),
            @AttributeOverride(name = "address", column = @Column(name = "loc_address")),
            @AttributeOverride(name = "complement", column = @Column(name = "loc_complement")),
            @AttributeOverride(name = "number", column = @Column(name = "loc_number")),
            @AttributeOverride(name = "neighborhood", column = @Column(name = "loc_neighborhood")),
            @AttributeOverride(name = "city", column = @Column(name = "loc_city")),
            @AttributeOverride(name = "state", column = @Column(name = "loc_state")),

    })
    private EventLocalization eventLocalization;
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EventTicket> tickets = new ArrayList<>();
}
