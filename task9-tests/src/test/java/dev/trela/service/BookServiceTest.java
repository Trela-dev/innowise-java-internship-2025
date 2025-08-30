package dev.trela.service;



import dev.trela.model.Author;
import dev.trela.model.Book;
import dev.trela.model.Genre;
import dev.trela.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    @Mock
    private AuthorService authorService;

    @Mock
    private GenreService genreService;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    BookService bookService;


    @Test
    void shouldSaveBookWithProcessedAuthorAndGenre(){
        Author inputAuthor1 = new Author(null,"Author One");
        Author inputAuthor2 = new Author(null,"Author Two");

        Author resolvedAuthor1 = new Author(1,"Author One");
        Author resolvedAuthor2 = new Author(2,"Author Two");

        Genre inputGenre = new Genre(null, "Fantasy");
        Genre resolvedGenre = new Genre(5,"Fantasy");

        Book inputBook = new Book(
                "Title",
                "Desc",
                123,
                new BigDecimal("4.2"),
                List.of(inputAuthor1,inputAuthor2),
                inputGenre
        );

        when(authorService.addAuthorIfNotExists("Author One"))
                .thenReturn(resolvedAuthor1);
        when(authorService.addAuthorIfNotExists("Author Two"))
                .thenReturn(resolvedAuthor2);
        when(genreService.getGenreByName("Fantasy"))
                .thenReturn(resolvedGenre);

        bookService.addBook(inputBook);

        ArgumentCaptor<Book> captor = ArgumentCaptor.forClass(Book.class);
        verify(bookRepository).save(captor.capture());

        Book savedBook = captor.getValue();
        assertNull(savedBook.getId());
        assertEquals("Title", savedBook.getTitle());
        assertEquals("Desc", savedBook.getDescription());
        assertEquals(123,savedBook.getPages());
        assertEquals(new BigDecimal("4.2"), savedBook.getRating());

        assertEquals(List.of(resolvedAuthor1,resolvedAuthor2), savedBook.getAuthors());
        assertEquals(resolvedGenre,savedBook.getGenre());

    }

}
