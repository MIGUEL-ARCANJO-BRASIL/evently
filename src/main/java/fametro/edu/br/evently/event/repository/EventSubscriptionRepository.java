package fametro.edu.br.evently.event.repository;

import fametro.edu.br.evently.event.model.EventSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EventSubscriptionRepository extends JpaRepository<EventSubscription, UUID> {

    Optional<EventSubscription> findByEvent_IdAndUserEmail(UUID eventId, String userEmail);
    Optional<EventSubscription> findByEvent_IdAndUserCpf(UUID eventId, String cpf);

}
