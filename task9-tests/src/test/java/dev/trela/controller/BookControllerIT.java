package dev.trela.controller;

import dev.trela.model.Author;
import dev.trela.model.Book;
import dev.trela.model.Genre;
import dev.trela.model.User;
import org.apache.coyote.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;


import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class BookControllerIT extends IntegrationTestBase {

    private String token;
    private HttpHeaders headers;

    @BeforeEach
    void setUp() {
        User user = new User("testuser", "password123", null);
        restTemplate.postForEntity("http://localhost:" + port + "/api/auth/register", user, String.class);

        ResponseEntity<String> loginResponse = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/auth/login",
                user,
                String.class
        );

        String authHeader = loginResponse.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        } else {
            throw new IllegalStateException("Brak tokena JWT w nagłówku Authorization");
        }

        headers = new HttpHeaders();
        headers.setBearerAuth(token);
    }


    @Test
    void shouldReturnCreatedBook() {
        Book newBook = new Book("Title",
                "Desc",
                100,
                BigDecimal.valueOf(4.5),
                List.of(new Author("Example Author")),
                new Genre(null, "Fantasy"));

        HttpEntity<Book> requestEntityWithJWTToken = new HttpEntity<>(newBook,headers);

        ResponseEntity<Book> postResponse = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/books",
                requestEntityWithJWTToken,
                Book.class
        );
        assertEquals(HttpStatus.CREATED, postResponse.getStatusCode());
        Book createdBook = postResponse.getBody();
        assertNotNull(createdBook);
        assertNotNull(createdBook.getId());
        assertEquals("Title", createdBook.getTitle());
        assertEquals("Desc", createdBook.getDescription());
        assertEquals(100, createdBook.getPages());
        assertEquals(BigDecimal.valueOf(4.5), createdBook.getRating());
        assertEquals("Fantasy", createdBook.getGenre().getName());
        assertEquals(1, createdBook.getAuthors().size());
        assertEquals("Example Author", createdBook.getAuthors().get(0).getName());
    }


    @Test
    void shouldReturnBookById(){
        Book newBook = new Book("Title",
                "Desc",
                100,
                BigDecimal.valueOf(4.5),
                List.of(new Author("Example Author")),
                new Genre(null, "Fantasy"));

        HttpEntity<Book> RequestEntityWithJWTToken = new HttpEntity<>(newBook,headers);
        // make sure there is at least 1 book
       restTemplate.postForEntity(
                "http://localhost:" + port + "/api/books",
                RequestEntityWithJWTToken,
                Book.class
        );

       ResponseEntity<Book> response =  restTemplate.exchange("http://localhost:" + port + "/api/books/1",
                HttpMethod.GET,
                RequestEntityWithJWTToken,
                Book.class);

       assertEquals(HttpStatus.OK, response.getStatusCode());

    }


    @Test
    void shouldGetAllBooks(){
        Book newBook = new Book("Title",
                "Desc",
                100,
                BigDecimal.valueOf(4.5),
                List.of(new Author("Example Author")),
                new Genre(null, "Fantasy"));

        HttpEntity<Book> requestEntityWithJWTToken = new HttpEntity<>(newBook,headers);

        restTemplate.postForEntity(
                "http://localhost:" + port + "/api/books",
                requestEntityWithJWTToken,
                Book.class
        );

        ResponseEntity<List<Book>> response =  restTemplate.exchange("http://localhost:" + port + "/api/books",
                HttpMethod.GET,
                requestEntityWithJWTToken,
                new ParameterizedTypeReference<>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<Book> books = response.getBody();
        assertNotNull(books);
        assertTrue(books.size()>0);
    }










}
