package uz.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.app.entity.OrderedProduct;

import java.util.UUID;

@Repository
public interface OrderedProductRepository extends JpaRepository<OrderedProduct, UUID> {
}
