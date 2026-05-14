package fametro.edu.br.evently.event.repository;

import fametro.edu.br.evently.event.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {

    @Query("SELECT c FROM Category c WHERE name = :name")
    Optional<Category> findbyName(@Param("name") String name);
}
