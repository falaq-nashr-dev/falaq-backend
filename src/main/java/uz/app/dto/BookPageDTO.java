package uz.app.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class BookPageDTO {
    private UUID bookId;
    private Integer pageNumber;
    private String content;
}
