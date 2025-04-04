package uz.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import uz.app.entity.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class OrderResponseDTO {
    private UUID orderId;
    private String customerFullName;
    private String customerPhoneNumber;
    private List<OrderProductResponseDTO> products;
    private double totalPrice;
    private LocalDateTime createdAt;
    private OrderStatus status;

    @Data
    @Builder
    @AllArgsConstructor
    public static class OrderProductResponseDTO {
        private UUID productId;
        private String productName;
        private int quantity;
        private double price;
        private double totalPrice;
    }
}