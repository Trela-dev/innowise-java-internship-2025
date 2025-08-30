package dev.trela.testing.service;

import dev.trela.testing.model.Book;
import dev.trela.testing.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class BookService {


    private final MessageService messageService;
    private final BookRepository bookRepositoryDb;


    public BookService(MessageService messageService, BookRepository bookRepositoryDb){
        this.messageService = messageService;
        this.bookRepositoryDb = bookRepositoryDb;
    }

    public List<Book> getAllBooks(){
        return bookRepositoryDb.findAll();
    }

    public void addBook(Book book) throws IllegalArgumentException{
        bookRepositoryDb.save(book);
    }

    public void updateBook(Book book) throws NoSuchElementException{
        bookRepositoryDb.update(book);
    }

    public void deleteBook(int bookId) throws NoSuchElementException{
       bookRepositoryDb.deleteById(bookId);
    }

    public List<Book> searchByKeyword(String keyword){
        return bookRepositoryDb.searchByKeyword(keyword);

    }


    public void printLocalizedBooks(List<Book> books){

        String localizedTitle = messageService.getMessage("book.title");
        String localizedAuthor = messageService.getMessage("book.author");
        String localizedDescription = messageService.getMessage("book.description");
        String localizedGenre = messageService.getMessage("book.genre");
        String localizedPages = messageService.getMessage("book.pages");
        String localizedRating = messageService.getMessage("book.rating");
        String localizedBook = messageService.getMessage("book");


        for (Book book : books) {
            String bookInfo = String.format("%s { id=%d, %s='%s', %s='%s', %s='%s', %s='%s', %s=%d, %s=%.1f }",
                    localizedBook,
                    book.getId(),
                    localizedTitle, book.getTitle(),
                    localizedAuthor, book.getAuthors(),
                    localizedDescription, book.getDescription(),
                    localizedGenre, book.getGenre().getName(),
                    localizedPages, book.getPages(),
                    localizedRating, book.getRating());
            System.out.println(bookInfo);
        }

    }

    public void validateRating(BigDecimal rating) {
        BigDecimal minRating = new BigDecimal("0");
        BigDecimal maxRating = new BigDecimal("5");

        if (rating.compareTo(minRating) < 0 || rating.compareTo(maxRating) > 0) {
            throw new IllegalArgumentException("Rating must be between 0 and 5.");
        }
    }





}
