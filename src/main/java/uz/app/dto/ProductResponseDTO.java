package uz.app.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class ProductResponseDTO {
    private String name;
    private UUID productTypeId;
    private UUID productCategoryId;
    private UUID authorId;
    private Double price;
    private Double salePrice;
    private Integer quantity;
    private String description;
    private String about;
    private Double rating;
}