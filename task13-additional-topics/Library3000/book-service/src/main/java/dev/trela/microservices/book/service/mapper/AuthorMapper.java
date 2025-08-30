package dev.trela.microservices.book.service.mapper;

import dev.trela.microservices.book.dto.AuthorDTO;
import dev.trela.microservices.book.model.Author;

public class AuthorMapper {
    public static AuthorDTO toDTO(Author author) {
        return new AuthorDTO(author.getId(), author.getName());
    }
    public static Author toEntity(AuthorDTO authorDTO) {
        return new Author(authorDTO.id(), authorDTO.name());
    }
}
