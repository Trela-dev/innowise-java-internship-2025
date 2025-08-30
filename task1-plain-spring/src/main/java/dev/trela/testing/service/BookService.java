package dev.trela.testing.service;
import dev.trela.testing.model.Book;
import dev.trela.testing.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository){
        this.bookRepository = bookRepository;
    }

    public List<Book> getAllBooks(){
        return bookRepository.getAllBooks();
    }

    public boolean addBook(Book book){
        return bookRepository.addBook(book);
    }

    public void updateBook(Book book) throws IllegalArgumentException, NoSuchElementException{
        bookRepository.updateBook(book);
    }

    public boolean deleteBook(int bookId){
        return bookRepository.deleteBook(bookId);
    }

    public List<Book> searchByKeyword(String keyword){
        return bookRepository.searchByKeyword(keyword);
    }

}
