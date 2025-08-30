package dev.trela.testing.repository;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import dev.trela.testing.model.Book;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Repository class that manages books stored in a CSV file.
 * It allows retrieving, adding, updating, and deleting books.
 */
@Repository // Marks this class as a Spring repository (data access layer)
public class BookRepository {

    // Path to the CSV file that stores book data
    private static final String CSV_FILE_PATH = "src/main/resources/books.csv";

    // Jackson CSV Mapper to read/write CSV files
    private final CsvMapper csvMapper = new CsvMapper();

    /**
     * Defines the CSV schema, specifying column names and data types.
     */
    private final CsvSchema schema = CsvSchema.builder()
            .addColumn("id", CsvSchema.ColumnType.NUMBER)  // ID column (integer)
            .addColumn("title", CsvSchema.ColumnType.STRING) // Book title (string)
            .addColumn("author", CsvSchema.ColumnType.STRING) // Book author (string)
            .addColumn("description", CsvSchema.ColumnType.STRING) // Book description (string)
            .setUseHeader(true) // The first row in the CSV file is the header
            .setColumnSeparator(',') // Columns are separated by commas
            .setQuoteChar('"') // Text fields are enclosed in double quotes if needed
            .build();

    /**
     * Retrieves all books from the CSV file.
     *
     * @return a list of books
     */
    public List<Book> getAllBooks() {

        try {
            File file = new File(CSV_FILE_PATH);

            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }



            // If the file does not exist, return an empty list
            if (!file.exists()) return new ArrayList<>();

            // Read the file and map it to a list of Book objects
            return csvMapper.readerFor(Book.class).with(schema).<Book>readValues(file).readAll();
        } catch (IOException e) {
            throw new RuntimeException("Error reading CSV file", e);
        }
    }

    /**
     * Adds a new book to the CSV file.
     *
     * @param book the book object to add
     */
    public boolean addBook(Book book) {
        List<Book> books = getAllBooks(); // Retrieve the current list of books

        boolean isAnyFieldEmpty = book.getAuthor().equals("") || book.getDescription().equals("") || book.getDescription().equals("");

        if(isAnyFieldEmpty) return false;

        // Assign an ID to the new book (increment the last ID in the file)
        book.setId(books.isEmpty() ? 1 : books.get(books.size() - 1).getId() + 1);

        books.add(book); // Add the book to the list
        saveAllBooks(books); // Save the updated list to the file
        return true;
    }

    /**
     * Deletes a book by its ID.
     *
     * @param bookId the ID of the book to delete
     * @return true if the book was deleted, false if no book was found with the given ID
     */
    public boolean deleteBook(int bookId) {
        List<Book> books = getAllBooks(); // Retrieve the list of books

        // Use binary search to find the book (assuming IDs are sorted)
        int index = Collections.binarySearch(
                books,
                new Book(bookId, null, null, null),
                (book1, book2) -> Integer.compare(book1.getId(), book2.getId())
        );

        // If the book is found, remove it
        if (index >= 0) {
            books.remove(index);
            saveAllBooks(books); // Save the changes to the file
            return true;
        } else {
            return false; // No book found with the given ID
        }
    }

    /**
     * Updates an existing book.
     *
     * @param updatedBook the updated book object
     */
    public void updateBook(Book updatedBook) {

        boolean isAnyFieldEmpty = updatedBook.getAuthor().equals("") ||  updatedBook.getDescription().equals("") || updatedBook.getDescription().equals("");

        if (isAnyFieldEmpty){
            throw new IllegalArgumentException("Failed to update the book: All fields must be filled.");
        }

        List<Book> books = getAllBooks(); // Retrieve the list of books

        // Find the book with the given ID
        Optional<Book> optionalExistingBook = books.stream()
                .filter(book -> book.getId() == updatedBook.getId())
                .findFirst();

        if (optionalExistingBook.isPresent()) {
            int existingBookIndex = books.indexOf(optionalExistingBook.get());
            books.set(existingBookIndex, updatedBook); // Replace the old book with the updated version
            saveAllBooks(books); // Save the changes to the file

        } else {
            throw new NoSuchElementException("Failed to update the book: No book found with the given ID (" + updatedBook.getId() + ").");
        }
    }

    /**
     * Saves a list of books to the CSV file (overwrites the existing file).
     *
     * @param books the list of books to save
     */
    public void saveAllBooks(List<Book> books) {
        try {
            csvMapper.writer(schema).writeValue(new File(CSV_FILE_PATH), books);
        } catch (IOException e) {
            throw new RuntimeException("Error writing to CSV file", e);
        }
    }
    /**
     * Searches for books with given keyword
     *
     * @param keyword the list of books to save
     */

    public List<Book> searchByKeyword(String keyword){
        return getAllBooks().stream()
                .filter(book -> book.getTitle().toLowerCase().contains(keyword.toLowerCase()) ||
                        book.getAuthor().toLowerCase().contains(keyword.toLowerCase()) ||
                        book.getDescription().toLowerCase().contains(keyword.toLowerCase()))
                .toList();
    }


}
