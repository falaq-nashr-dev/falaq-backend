package uz.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.app.entity.Author;

import java.util.UUID;

public interface AuthorRepository extends JpaRepository<Author, UUID> {
}