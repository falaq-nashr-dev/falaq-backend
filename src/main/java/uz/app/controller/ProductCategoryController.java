package uz.app.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.app.dto.NameDTO;
import uz.app.entity.ProductCategory;
import uz.app.repository.ProductCategoryRepository;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/product-category")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
@Tag(name = "Product Category Management", description = "Only Admin & Super Admin can manage")
public class ProductCategoryController {
    private final ProductCategoryRepository productCategoryRepository;

    @GetMapping
    public ResponseEntity<List<ProductCategory>> getAll() {
        return ResponseEntity.ok(productCategoryRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<ProductCategory> addCategory(@RequestBody NameDTO categoryDTO) {
        ProductCategory category = new ProductCategory();
        category.setName(categoryDTO.getName());

        return ResponseEntity.ok(productCategoryRepository.save(category));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductCategory> getFromId(@PathVariable UUID id) {
        return productCategoryRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductCategory> update(@PathVariable UUID id, @RequestBody NameDTO categoryDTO) {
        return productCategoryRepository.findById(id)
                .map(existingCategory -> {
                    existingCategory.setName(categoryDTO.getName());
                    return ResponseEntity.ok(productCategoryRepository.save(existingCategory));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (productCategoryRepository.existsById(id)) {
            productCategoryRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
