package dev.trela.testing.repository;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import dev.trela.testing.model.Book;
import dev.trela.testing.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Repository class responsible for managing book data stored in a CSV file.
 * Provides functionality to retrieve, add, update, delete, and search books.
 */
@Repository // Indicates this class is part of the data access layer in Spring
public class BookRepository {

    // Path to the CSV file that stores the book records
    private static final String CSV_FILE_PATH = "src/main/resources/books.csv";

    // Jackson CSV Mapper for reading and writing CSV files
    private final CsvMapper csvMapper = new CsvMapper();

    // Service used to retrieve localized or custom error messages
    private MessageService messageService;

    @Autowired
    public void setMessageService(MessageService messageService){
        this.messageService = messageService;
    }

    // Defines the CSV schema including headers and column types
    private final CsvSchema schema = CsvSchema.builder()
            .addColumn("id", CsvSchema.ColumnType.NUMBER)         // Book ID (numeric)
            .addColumn("title", CsvSchema.ColumnType.STRING)      // Book title (text)
            .addColumn("author", CsvSchema.ColumnType.STRING)     // Book author (text)
            .addColumn("description", CsvSchema.ColumnType.STRING) // Book description (text)
            .setUseHeader(true)                                   // First row contains column headers
            .setColumnSeparator(',')                              // Columns separated by commas
            .setQuoteChar('"')                                    // Text fields may be enclosed in quotes
            .build();

    /**
     * Retrieves all books from the CSV file.
     *
     * @return a list of all books
     */
    public List<Book> getAllBooks() {
        try {
            File file = new File(CSV_FILE_PATH);

            // If the file doesn't exist, create it along with its parent directories
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }

            // Read the CSV and map it to a list of Book objects
            return csvMapper.readerFor(Book.class).with(schema).<Book>readValues(file).readAll();

        } catch (IOException e) {
            throw new RuntimeException(messageService.getMessage("error.reading.csv"), e);
        }
    }

    /**
     * Adds a new book to the CSV file.
     *
     * @param book the book to add
     * @throws IllegalArgumentException if any required fields are empty
     */
    public void addBook(Book book) throws IllegalArgumentException {
        List<Book> books = getAllBooks(); // Load existing books

        boolean isAnyFieldEmpty = book.getAuthor().equals("") ||
                book.getDescription().equals("") ||
                book.getTitle().equals("");

        if (isAnyFieldEmpty) {
            throw new IllegalArgumentException(messageService.getMessage("error.empty.fields"));
        }

        // Assign a unique ID to the new book
        book.setId(books.isEmpty() ? 1 : books.get(books.size() - 1).getId() + 1);

        books.add(book);         // Add the book to the list
        saveAllBooks(books);     // Write the updated list back to the CSV file
    }

    /**
     * Deletes a book by its ID.
     *
     * @param bookId the ID of the book to delete
     * @throws NoSuchElementException if no book with the given ID is found
     */
    public void deleteBook(int bookId) {
        List<Book> books = getAllBooks();

        // Use binary search assuming the list is sorted by ID
        int index = Collections.binarySearch(
                books,
                new Book(bookId, null, null, null),
                Comparator.comparingInt(Book::getId)
        );

        if (index >= 0) {
            books.remove(index);     // Remove the book from the list
            saveAllBooks(books);     // Save the updated list
        } else {
            throw new NoSuchElementException(messageService.getMessage("error.no.such.book"));
        }
    }

    /**
     * Updates the details of an existing book.
     *
     * @param updatedBook the book with updated information
     * @throws IllegalArgumentException if any fields are empty
     * @throws NoSuchElementException if the book doesn't exist
     */
    public void updateBook(Book updatedBook) {
        boolean isAnyFieldEmpty = updatedBook.getAuthor().equals("") ||
                updatedBook.getDescription().equals("") ||
                updatedBook.getTitle().equals("");

        if (isAnyFieldEmpty) {
            throw new IllegalArgumentException(messageService.getMessage("error.empty.fields"));
        }

        List<Book> books = getAllBooks();

        Optional<Book> optionalExistingBook = books.stream()
                .filter(book -> book.getId() == updatedBook.getId())
                .findFirst();

        if (optionalExistingBook.isPresent()) {
            int existingBookIndex = books.indexOf(optionalExistingBook.get());
            books.set(existingBookIndex, updatedBook);  // Replace old book with the updated one
            saveAllBooks(books);                        // Save changes
        } else {
            throw new NoSuchElementException(messageService.getMessage("error.no.such.book"));
        }
    }

    /**
     * Saves a list of books to the CSV file, overwriting existing content.
     *
     * @param books the list of books to write
     */
    public void saveAllBooks(List<Book> books) {
        try {
            csvMapper.writer(schema).writeValue(new File(CSV_FILE_PATH), books);
        } catch (IOException e) {
            throw new RuntimeException(messageService.getMessage("error.writing.csv"), e);
        }
    }

    /**
     * Searches for books that contain the specified keyword in their title,
     * author name, or description (case-insensitive).
     *
     * @param keyword the search keyword
     * @return a list of books matching the keyword
     */
    public List<Book> searchByKeyword(String keyword) {
        return getAllBooks().stream()
                .filter(book -> book.getTitle().toLowerCase().contains(keyword.toLowerCase()) ||
                        book.getAuthor().toLowerCase().contains(keyword.toLowerCase()) ||
                        book.getDescription().toLowerCase().contains(keyword.toLowerCase()))
                .toList();
    }
}
