package uz.app.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.app.dto.*;
import uz.app.entity.Product;
import uz.app.entity.ProductRating;
import uz.app.entity.User;
import uz.app.repository.ProductRatingRepository;
import uz.app.repository.ProductRepository;
import uz.app.util.UserUtil;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/product-rating")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@Tag(name = "Product Rating Management", description = "Every logged in User can manage")
public class ProductRatingController {
    private final ProductRatingRepository productRatingRepository;
    private final ProductRepository productRepository;
    private final UserUtil userUtil;

    private ProductResponseDTO convertToProductDTO(Product product) {
        Double averageRating = productRatingRepository.getAverageRatingByProductId(product.getId());

        ProductResponseDTO dto = new ProductResponseDTO();
        dto.setName(product.getName());
        dto.setProductTypeId(product.getProductType().getId());
        dto.setProductCategoryId(product.getProductCategory().getId());
        dto.setAuthorId(product.getAuthor().getId());
        dto.setPrice(product.getPrice());
        dto.setSalePrice(product.getSalePrice());
        dto.setQuantity(product.getQuantity());
        dto.setDescription(product.getDescription());
        dto.setAbout(product.getAbout());
        dto.setRating(averageRating != null ? averageRating : 0.0);
        return dto;
    }

    private ProductRatingResponseDTO convertToRatingDTO(ProductRating rating) {
        ProductRatingResponseDTO dto = new ProductRatingResponseDTO();
        dto.setId(rating.getId());
        dto.setUserId(rating.getUserId());
        dto.setRating(rating.getRating());
        dto.setReview(rating.getReview());
        dto.setProduct(convertToProductDTO(rating.getBook()));
        return dto;
    }

    @GetMapping
    public ResponseEntity<List<ProductRatingResponseDTO>> getAll() {
        List<ProductRatingResponseDTO> ratings = productRatingRepository.findAll()
                .stream()
                .map(this::convertToRatingDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ratings);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductRatingResponseDTO> getFromId(@PathVariable UUID id) {
        return productRatingRepository.findById(id)
                .map(this::convertToRatingDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ProductRatingResponseDTO>> getRatingsByProductId(@PathVariable UUID productId) {
        List<ProductRatingResponseDTO> ratings = productRatingRepository.findByBookId(productId)
                .stream()
                .map(this::convertToRatingDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ratings);
    }

    @PostMapping
    public ResponseEntity<?> addRating(@RequestBody ProductRatingDTO ratingDTO) {
        Optional<User> currentUser = userUtil.getCurrentUser();
        if (currentUser.isEmpty()) {
            return ResponseEntity.status(401).body("User is not authenticated");
        }

        Optional<Product> optionalProduct = productRepository.findById(ratingDTO.getProductId());
        if (optionalProduct.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid product ID");
        }

        Product product = optionalProduct.get();
        ProductRating rating = new ProductRating();
        rating.setBook(product);
        rating.setUserId(currentUser.get().getId());
        rating.setRating(ratingDTO.getRating());
        rating.setReview(ratingDTO.getReview());

        productRatingRepository.save(rating);

        Double newAverage = productRatingRepository.getAverageRatingByProductId(product.getId());
        product.setRating(newAverage != null ? newAverage : 0.0);
        productRepository.save(product);

        return ResponseEntity.ok(convertToRatingDTO(rating));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody ProductRatingDTO ratingDTO) {
        Optional<User> currentUser = userUtil.getCurrentUser();
        if (currentUser.isEmpty()) {
            return ResponseEntity.status(401).body("User is not authenticated");
        }

        return productRatingRepository.findById(id)
                .map(existingRating -> {
                    if (!existingRating.getUserId().equals(currentUser.get().getId())) {
                        return ResponseEntity.status(403).body("You can only edit your own ratings");
                    }

                    return productRepository.findById(ratingDTO.getProductId())
                            .map(product -> {
                                existingRating.setBook(product);
                                existingRating.setRating(ratingDTO.getRating());
                                existingRating.setReview(ratingDTO.getReview());
                                productRatingRepository.save(existingRating);

                                // Update average rating
                                Double newAverage = productRatingRepository.getAverageRatingByProductId(product.getId());
                                product.setRating(newAverage != null ? newAverage : 0.0);
                                productRepository.save(product);

                                return ResponseEntity.ok(convertToRatingDTO(existingRating));
                            })
                            .orElseGet(() -> ResponseEntity.notFound().build());
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        Optional<User> currentUser = userUtil.getCurrentUser();
        if (currentUser.isEmpty()) {
            return ResponseEntity.status(401).body("User is not authenticated");
        }

        return productRatingRepository.findById(id)
                .map(existingRating -> {
                    if (!existingRating.getUserId().equals(currentUser.get().getId())) {
                        return ResponseEntity.status(403).body("You can only delete your own ratings");
                    }

                    Product product = existingRating.getBook();
                    productRatingRepository.deleteById(id);

                    // Update average rating after deletion
                    Double newAverage = productRatingRepository.getAverageRatingByProductId(product.getId());
                    product.setRating(newAverage != null ? newAverage : 0.0);
                    productRepository.save(product);

                    return ResponseEntity.ok().build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}