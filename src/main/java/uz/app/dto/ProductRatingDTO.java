package uz.app.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class ProductRatingDTO {
    private UUID productId;
    private Integer rating; // 1-5
    private String review; // Can be null
}
