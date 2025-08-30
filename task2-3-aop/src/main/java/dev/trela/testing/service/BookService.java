package dev.trela.testing.service;
import dev.trela.testing.model.Book;
import dev.trela.testing.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final MessageService messageService;

    public BookService(BookRepository bookRepository,MessageService messageService){
        this.messageService = messageService;
        this.bookRepository = bookRepository;
    }

    public List<Book> getAllBooks(){
        return bookRepository.getAllBooks();
    }

    public void addBook(Book book) throws IllegalArgumentException{
        bookRepository.addBook(book);
    }


    public void updateBook(Book book) throws IllegalArgumentException, NoSuchElementException{
        bookRepository.updateBook(book);
    }

    public void deleteBook(int bookId) throws NoSuchElementException{
        bookRepository.deleteBook(bookId);
    }

    public List<Book> searchByKeyword(String keyword){
       List<Book> foundBooks =  bookRepository.searchByKeyword(keyword);
        return foundBooks;
    }


    public void printLocalizedBooks(List<Book> books){

        String localizedTitle = messageService.getMessage("book.title");
        String localizedAuthor = messageService.getMessage("book.author");
        String localizedDescription = messageService.getMessage("book.description");
        String localizedBook = messageService.getMessage("book");

        for(Book book : books){
            System.out.println(localizedBook + "{" +
                    "id=" + book.getId() +
                    ", " + localizedTitle + "='" + book.getTitle() + '\'' +
                    ", " + localizedAuthor + "='" + book.getAuthor() + '\'' +
                    ", " + localizedDescription + "='" + book.getDescription() + '\'' +
                    '}');
        }

    }




}
