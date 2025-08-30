import dev.trela.testing.model.Book;
import dev.trela.testing.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BookRepositoryTest {

    private BookRepository bookRepository;

    @BeforeEach
    void setUp(){
        // Initialize BookRepository and populate it with test data before each test
        this.bookRepository = new BookRepository();
        List<Book> testBooks = List.of(
                new Book(1, "Hobbit", "J.R.R. Tolkien", "Fantasy novel about Bilbo Baggins"),
                new Book(2, "1984", "George Orwell", "Dystopian fiction about a totalitarian regime"),
                new Book(3, "Dziady", "Adam Mickiewicz", "Polish dramatic poem about spirits and the afterlife")
        );
        bookRepository.saveAllBooks(testBooks);
    }

    @Test
    void testGetAllBooks(){
        // Ensure that all books are retrieved correctly
        List<Book> books = bookRepository.getAllBooks();
        assertEquals(3, books.size());
    }

    @Test
    void testSaveAllBooks(){
        // Save a new list of books and verify that the repository contains only the new list
        List<Book> books = List.of(new Book(1,"book","book","book"));
        bookRepository.saveAllBooks(books);
        assertEquals(1, bookRepository.getAllBooks().size());
    }

    @Test
    void testDeleteBook(){
        // Ensure a book is successfully deleted and returns true
        assertTrue(bookRepository.deleteBook(1));
    }

    @Test
    void testUpdateBook() {
        // Ensure updating a book with valid data does not throw an exception
        assertDoesNotThrow(() -> bookRepository.updateBook(new Book(1, "TEST", "TEST", "TEST")));

        // Ensure updating a book with empty fields throws an IllegalArgumentException
        assertThrows(IllegalArgumentException.class,
                () -> bookRepository.updateBook(new Book(1, "", "", "TEST")));
    }

    @Test
    void testAddBook(){
        // Ensure a new book is added successfully
        bookRepository.addBook(new Book(-300,"TEST","TEST","TEST"));
        assertEquals(4, bookRepository.getAllBooks().size());

        // Ensure adding a book with empty fields fails (returns false)
        assertFalse(bookRepository.addBook(new Book(-300,"","","")));
    }
}
