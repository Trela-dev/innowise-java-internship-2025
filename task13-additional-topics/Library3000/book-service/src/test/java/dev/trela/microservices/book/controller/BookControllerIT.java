package dev.trela.microservices.book.controller;

import dev.trela.microservices.book.TestcontainersConfiguration;
import dev.trela.microservices.book.dto.BookDTO;
import dev.trela.microservices.book.model.Author;
import dev.trela.microservices.book.model.Book;
import dev.trela.microservices.book.model.Genre;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
public class BookControllerIT {

    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void shouldReturnCreatedBook() {
        Book newBook = new Book("Title",
                "Desc",
                100,
                BigDecimal.valueOf(4.5),
                List.of(new Author("Example Author")),
                new Genre(null, "Fantasy"));

        HttpEntity<Book> requestEntity = new HttpEntity<>(newBook);

        ResponseEntity<BookDTO> postResponse = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/books",
                requestEntity,
                BookDTO.class
        );

        assertEquals(HttpStatus.CREATED, postResponse.getStatusCode());
        BookDTO createdBook = postResponse.getBody();
        assertNotNull(createdBook);
        assertNotNull(createdBook.id());
        assertEquals("Title", createdBook.title());
    }

    @Test
    void shouldReturnBookById() {
        Book newBook = new Book("Title",
                "Desc",
                100,
                BigDecimal.valueOf(4.5),
                List.of(new Author("Example Author")),
                new Genre(null, "Fantasy"));

        restTemplate.postForEntity(
                "http://localhost:" + port + "/api/books",
                new HttpEntity<>(newBook),
                BookDTO.class
        );

        ResponseEntity<BookDTO> response = restTemplate.exchange(
                "http://localhost:" + port + "/api/books/1",
                HttpMethod.GET,
                null,
                BookDTO.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void shouldGetAllBooks() {
        Book newBook = new Book("Title",
                "Desc",
                100,
                BigDecimal.valueOf(4.5),
                List.of(new Author("Example Author")),
                new Genre(null, "Fantasy"));

        restTemplate.postForEntity(
                "http://localhost:" + port + "/api/books",
                new HttpEntity<>(newBook),
                BookDTO.class
        );

        ResponseEntity<List<BookDTO>> response = restTemplate.exchange(
                "http://localhost:" + port + "/api/books",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<BookDTO>>() {}
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<BookDTO> books = response.getBody();
        assertNotNull(books);
        assertFalse(books.isEmpty());
    }
}
