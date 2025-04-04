package uz.app.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.app.dto.NameDTO;
import uz.app.entity.ProductType;
import uz.app.repository.ProductTypeRepository;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/product-type")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
@Tag(name = "Product Type Management", description = "Only Admin & Super Admin can manage")
public class ProductTypeController {
    private final ProductTypeRepository productTypeRepository;

    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(productTypeRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<ProductType> addType(@RequestBody NameDTO typeDTO) {
        ProductType type = new ProductType();
        type.setName(typeDTO.getName());

        return ResponseEntity.ok(productTypeRepository.save(type));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductType> getFromId(@PathVariable UUID id) {
        return productTypeRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductType> update(@PathVariable UUID id, @RequestBody NameDTO typeDTO) {
        return productTypeRepository.findById(id)
                .map(existingType -> {
                    existingType.setName(typeDTO.getName());
                    return ResponseEntity.ok(productTypeRepository.save(existingType));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (productTypeRepository.existsById(id)) {
            productTypeRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
