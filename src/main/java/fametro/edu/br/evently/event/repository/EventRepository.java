package fametro.edu.br.evently.event.repository;

import fametro.edu.br.evently.event.enums.EventStatus;
import fametro.edu.br.evently.event.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {
    List<Event> findByOrganizerId(UUID organizerId);

    List<Event> findAllByEventStatus(EventStatus eventStatus);
}