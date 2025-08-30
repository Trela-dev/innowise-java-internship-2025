package dev.trela.microservices.book.service.mapper;

import dev.trela.microservices.book.dto.GenreDTO;
import dev.trela.microservices.book.model.Genre;

public class GenreMapper {

    public static GenreDTO toDTO(Genre genre) {
        return new GenreDTO(genre.getId(), genre.getName());
    }
    public static Genre toEntity(GenreDTO genre) {
        return new Genre(genre.id(), genre.name());
    }
}
