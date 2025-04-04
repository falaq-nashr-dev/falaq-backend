package uz.app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "product_ratings")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProductRating {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "UUID")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Product book;

    private UUID userId;
    private Integer rating; // 1-5
    private String review;
}
