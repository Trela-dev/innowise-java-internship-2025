package dev.trela.microservices.book.service.mapper;

import dev.trela.microservices.book.dto.AuthorDTO;
import dev.trela.microservices.book.dto.BookDTO;
import dev.trela.microservices.book.dto.GenreDTO;
import dev.trela.microservices.book.model.Author;
import dev.trela.microservices.book.model.Book;
import dev.trela.microservices.book.model.Genre;

import java.util.List;
import java.util.stream.Collectors;

public class BookMapper {

    public static BookDTO toDTO(Book book){


        List<AuthorDTO> authorsDTOList = book.getAuthors().stream()
                .map(AuthorMapper:: toDTO)
                .toList();

        return new BookDTO(
                book.getId(),
                book.getTitle(),
                book.getDescription(),
                book.getPages(),
                book.getRating(),
                authorsDTOList,
                GenreMapper.toDTO(book.getGenre()),
                book.getImageFileId()
        );

    }

    public static Book toEntity(BookDTO bookDTO) {


       List<Author> authorsList = bookDTO.authors().stream()
               .map(AuthorMapper::toEntity)
               .toList();

        return new Book(
                bookDTO.title(),
                bookDTO.description(),
                bookDTO.pages(),
                bookDTO.rating(),
                authorsList,
                GenreMapper.toEntity(bookDTO.genre())
        );
    }

}
