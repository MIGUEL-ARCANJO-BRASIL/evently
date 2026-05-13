package fametro.edu.br.evently.event.repository;

import fametro.edu.br.evently.event.model.EventSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EventSubscriptionRepository extends JpaRepository<EventSubscription, UUID> {

    Optional<EventSubscription> findByEvent_IdAndUserEmail(UUID eventId, String userEmail);
    Optional<EventSubscription> findByEvent_IdAndUserCpf(UUID eventId, String cpf);
    Optional<List<EventSubscription>> findByUserEmail(String email);

    @org.springframework.data.jpa.repository.Query("SELECT COALESCE(SUM(es.paidValue), 0.0) FROM EventSubscription es WHERE es.event.organizer.id = :organizerId")
    Double sumPaidValueByOrganizerId(@org.springframework.data.repository.query.Param("organizerId") UUID organizerId);

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(es) FROM EventSubscription es WHERE es.event.organizer.id = :organizerId")
    Long countByOrganizerId(@org.springframework.data.repository.query.Param("organizerId") UUID organizerId);
}
