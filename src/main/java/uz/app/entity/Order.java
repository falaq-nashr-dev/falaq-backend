package uz.app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.app.config.LocalDateTimeAttributeConverter;
import uz.app.entity.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "UUID")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    private String customerFullName;
    private String customerPhoneNumber;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderedProduct> orderedProducts;

    private Double totalPrice;

    @Convert(converter = LocalDateTimeAttributeConverter.class)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;
}