package fametro.edu.br.evently.event.repository;

import fametro.edu.br.evently.event.model.EventTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EventTicketRepository extends JpaRepository<EventTicket, UUID> {
}
