package fametro.edu.br.evently.event.repository;

import fametro.edu.br.evently.event.model.EventTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EventTransactionRepository extends JpaRepository<EventTransaction, UUID> {
    Optional<EventTransaction> findBySubscription_Id(UUID subscriptionId);
}
