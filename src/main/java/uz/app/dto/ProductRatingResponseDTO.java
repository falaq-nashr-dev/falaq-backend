package uz.app.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class ProductRatingResponseDTO {
    private UUID id;
    private UUID userId;
    private ProductResponseDTO product;
    private int rating;
    private String review;
}