package uz.app.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.app.entity.Author;
import uz.app.repository.AuthorRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/authors")
@RequiredArgsConstructor
@Tag(name = "Author Management", description = "Only Admin & Super Admin can manage")
@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
public class AuthorController {
    private final AuthorRepository authorRepository;

    @GetMapping
    public ResponseEntity<List<Author>> getAllAuthors() {
        return ResponseEntity.ok(authorRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Author> getAuthorById(@PathVariable UUID id) {
        return authorRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Author> createAuthor(@RequestBody Author author) {
        if (author.getFullName() == null || author.getFullName().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(authorRepository.save(author));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Author> updateAuthor(@PathVariable UUID id, @RequestBody Author authorDetails) {
        Optional<Author> existingAuthor = authorRepository.findById(id);
        if (existingAuthor.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Author authorToUpdate = existingAuthor.get();
        if (authorDetails.getFullName() != null && !authorDetails.getFullName().isBlank()) {
            authorToUpdate.setFullName(authorDetails.getFullName());
        }
        if (authorDetails.getDefinition() != null) {
            authorToUpdate.setDefinition(authorDetails.getDefinition());
        }

        return ResponseEntity.ok(authorRepository.save(authorToUpdate));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuthor(@PathVariable UUID id) {
        if (authorRepository.existsById(id)) {
            authorRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}