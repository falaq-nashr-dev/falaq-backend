package uz.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.app.entity.ProductType;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductTypeRepository extends JpaRepository<ProductType, UUID> {
    Optional<ProductType> findByName(String typeName);
}
