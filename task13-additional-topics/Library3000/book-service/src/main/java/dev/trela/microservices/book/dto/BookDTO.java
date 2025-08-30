package dev.trela.microservices.book.dto;

import java.math.BigDecimal;
import java.util.List;

public record BookDTO(
        Integer id,
        String title,
        String description,
        int pages,
        BigDecimal rating,
        List<AuthorDTO> authors,
        GenreDTO genre,
        String imageFileId
) {
}
