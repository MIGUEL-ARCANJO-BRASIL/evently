package fametro.edu.br.evently.event.repository;

import fametro.edu.br.evently.event.model.SubscriptionItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SubscriptionItemRepository extends JpaRepository<SubscriptionItem, UUID> {

    @Query("SELECT COALESCE(SUM(si.quantity), 0) FROM SubscriptionItem si WHERE si.ticket.id = :ticketId")
    Integer sumQuantityByTicketId(@Param("ticketId") UUID ticketId);

    @Query("SELECT COALESCE(SUM(si.quantity), 0) FROM SubscriptionItem si WHERE si.ticket.event.organizer.id = :organizerId")
    Long sumQuantityByOrganizerId(@Param("organizerId") UUID organizerId);
}
