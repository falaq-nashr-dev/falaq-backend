package uz.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.app.entity.ProductRating;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductRatingRepository extends JpaRepository<ProductRating, UUID> {
    List<ProductRating> findByBookId(UUID productId);

    @Query("SELECT AVG(pr.rating) FROM ProductRating pr WHERE pr.book.id = :productId")
    Double getAverageRatingByProductId(@Param("productId") UUID productId);
}
