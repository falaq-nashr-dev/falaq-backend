package uz.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.app.entity.Product;
import uz.app.entity.ProductType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    List<Product> findTop5ByProductTypeOrderByIdDesc(ProductType productType);

    List<Product> findByProductType(ProductType productType);

    @Query(value = "SELECT * FROM products p WHERE " +
            "LOWER(p.name) LIKE LOWER(:query)", nativeQuery = true)
    List<Product> searchAllProducts(@Param("query") String query);

    @Query(value = "SELECT * FROM products p WHERE " +
            "p.product_type_id = :typeId AND " +
            "LOWER(p.name) LIKE LOWER(:query)", nativeQuery = true)
    List<Product> searchByProductType(@Param("typeId") UUID typeId, @Param("query") String query);
}