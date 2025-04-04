package uz.app.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class OrderProductDTO {
    private UUID productId;
    private Integer amount;
}
