package uz.app.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class ProductRatingResponseDTO {
    private UUID id;
    private String userFullName;
    private ProductResponseDTO product;
    private int rating;
    private String review;
}