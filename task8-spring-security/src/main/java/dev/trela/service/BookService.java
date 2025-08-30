package dev.trela.service;

import dev.trela.model.Author;
import dev.trela.model.Book;
import dev.trela.repository.BookRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class BookService {


    private final MessageService messageService;
    private final BookRepository bookRepository;

    private final AuthorService authorService;
    private final GenreService genreService;


    public BookService(MessageService messageService,
                       BookRepository bookRepositoryJPA,
                       AuthorService authorService,
                       GenreService genreService){
        this.messageService = messageService;
        this.bookRepository = bookRepositoryJPA;
        this.authorService = authorService;
        this.genreService = genreService;


    }

    public List<Book> getAllBooks(){
        return bookRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }

    public Book getBookById(int id){
        return bookRepository.findById(id).orElseThrow(()->
                new NoSuchElementException(messageService.getMessage("error.no.such.book")));
    }


    public void addBook(Book book) throws IllegalArgumentException{
        book.setId(null);
        List<Author> resolvedAuthors = book.getAuthors().stream()
                        .map(author -> authorService.addAuthorIfNotExists(author.getName()))
                                .toList();

        book.setAuthors(resolvedAuthors);
        String genreName = book.getGenre().getName();
        book.setGenre(genreService.getGenreByName(genreName));
        System.out.println(book.getGenre());
        bookRepository.save(book);
    }

    public Book updateBook(Integer id, Book updatedBook) throws NoSuchElementException{
        Book existingBook = getBookById(id);

        List<Author> resolvedAuthors = updatedBook.getAuthors().stream()
                .map(author -> authorService.addAuthorIfNotExists(author.getName()))
                .toList();

        existingBook.getAuthors().clear();
        existingBook.getAuthors().addAll(resolvedAuthors);
        existingBook.setDescription(updatedBook.getDescription());
        existingBook.setPages(updatedBook.getPages());
        existingBook.setRating(updatedBook.getRating());
        String genreName = updatedBook.getGenre().getName();
        existingBook.setGenre(genreService.getGenreByName(genreName));
        existingBook.setTitle(updatedBook.getTitle());
        //
        existingBook.setImageFileId(updatedBook.getImageFileId());
        //
       return bookRepository.save(existingBook);
    }

    public void deleteBook(int bookId) throws NoSuchElementException{
       getBookById(bookId);
        bookRepository.deleteById(bookId);
    }

    public List<Book> searchByKeyword(String keyword){
        return bookRepository.searchByKeyword(keyword);
    }


}
