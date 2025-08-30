package dev.trela.service;

import dev.trela.model.Book;
import dev.trela.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class BookService {


    private final MessageService messageService;
    private final BookRepository bookRepository;



    public BookService(MessageService messageService, BookRepository bookRepositoryDb){
        this.messageService = messageService;
        this.bookRepository = bookRepositoryDb;

    }


    public List<Book> getAllBooks(){
        return bookRepository.findAll();
    }

    public Book getBookById(int id){
        return bookRepository.findById(id).orElseThrow(()->
                new NoSuchElementException(messageService.getMessage("error.no.such.book")));
    }


    @Transactional
    public void addBook(Book book) throws IllegalArgumentException{
        bookRepository.save(book);
    }

    @Transactional
    public void updateBook(Book book) throws NoSuchElementException{
        bookRepository.update(book);
    }

    @Transactional
    public void deleteBook(int bookId) throws NoSuchElementException{
       bookRepository.deleteById(bookId);
    }

    public List<Book> searchByKeyword(String keyword){
        return bookRepository.searchByKeyword(keyword);

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
            throw new IllegalArgumentException(messageService.getMessage("error.rating.range"));
        }
    }






}
