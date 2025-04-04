package uz.app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "products")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "UUID")
    private UUID id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "product_type_id", nullable = false)
    private ProductType productType;

    @ManyToOne
    @JoinColumn(name = "product_category_id", nullable = false)
    private ProductCategory productCategory;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private Author author;

    @OneToOne(cascade = CascadeType.REMOVE)
    private Attachment photo;

    private Double rating;
    private Double price;
    private Double salePrice;
    private Integer quantity;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String about;
}
