package uz.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.app.entity.BookPage;

import java.util.List;
import java.util.UUID;

@Repository
public interface BookPageRepository extends JpaRepository<BookPage, UUID> {
    List<BookPage> findByBookId(UUID bookId);
}
