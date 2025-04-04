package uz.app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "book_pages")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BookPage {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "UUID")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Product book;

    private Integer pageNumber;

    @Column(columnDefinition = "TEXT")
    private String content;
}
