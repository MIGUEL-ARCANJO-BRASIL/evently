package fametro.edu.br.evently.event.repository;

import fametro.edu.br.evently.event.enums.EventStatus;
import fametro.edu.br.evently.event.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {
    List<Event> findByOrganizerId(UUID organizerId);

    List<Event> findAllByEventStatus(EventStatus eventStatus);
    List<Event> findAllByOrganizer_IdOrderByEventStatusDesc(UUID organizerId);

    @Query("SELECT e FROM Event e WHERE e.eventStatus = :status AND (CAST(:category AS String) IS NULL OR e.category" +
            ".name = CAST(:category AS String)) AND (CAST(:query AS String) IS NULL OR LOWER(e.title) LIKE LOWER" +
            "(CONCAT('%', CAST(:query AS String), '%'))) ORDER BY e.eventDate ASC")
    List<Event> findFilteredEvents(@Param("status") EventStatus status, @Param("category") String category, @Param("query") String query);

    @Query("SELECT e FROM Event e WHERE e.organizer.id = :organizerId AND (CAST(:category AS String) IS NULL OR e" +
            ".category.name = CAST(:category AS String)) AND (CAST(:query AS String) IS NULL OR LOWER(e.title) LIKE " +
            "LOWER(CONCAT('%', CAST(:query AS String), '%'))) ORDER BY e.eventStatus DESC")
    List<Event> findFilteredEventsByOrganizer(@Param("organizerId") UUID organizerId, @Param("category") String category, @Param("query") String query);
}