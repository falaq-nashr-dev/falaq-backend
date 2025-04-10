package uz.app.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.app.dto.BookPageDTO;
import uz.app.entity.BookPage;
import uz.app.entity.Product;
import uz.app.repository.BookPageRepository;
import uz.app.repository.ProductRepository;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/book-pages")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN','USER')")
@Tag(name = "Book Page Management", description = "Only Admin & Super Admin can manage")
public class BookPageController {
    private final BookPageRepository bookPageRepository;
    private final ProductRepository productRepository;

    @GetMapping
    public ResponseEntity<List<BookPage>> getAll() {
        return ResponseEntity.ok(bookPageRepository.findAll());
    }

    @GetMapping("/by-book/{bookId}")
    public ResponseEntity<?> getPagesByBookId(@PathVariable UUID bookId) {
        if (!productRepository.existsById(bookId)) {
            return ResponseEntity.badRequest().body("Book not found");
        }
        List<BookPage> pages = bookPageRepository.findByBookId(bookId);
        return ResponseEntity.ok(pages);
    }

    @PostMapping
    public ResponseEntity<?> addPage(@RequestBody BookPageDTO pageDTO) {
        Product book = productRepository.findById(pageDTO.getBookId())
                .orElse(null);

        if (book == null) {
            return ResponseEntity.badRequest().body("Book not found");
        }

        BookPage page = new BookPage();
        page.setBook(book);
        page.setPageNumber(pageDTO.getPageNumber());
        page.setContent(pageDTO.getContent());

        return ResponseEntity.ok(bookPageRepository.save(page));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookPage> getFromId(@PathVariable UUID id) {
        return bookPageRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookPage> update(@PathVariable UUID id, @RequestBody BookPageDTO pageDTO) {
        return bookPageRepository.findById(id)
                .map(existingPage -> {
                    existingPage.setPageNumber(pageDTO.getPageNumber());
                    existingPage.setContent(pageDTO.getContent());
                    return ResponseEntity.ok(bookPageRepository.save(existingPage));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (bookPageRepository.existsById(id)) {
            bookPageRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
