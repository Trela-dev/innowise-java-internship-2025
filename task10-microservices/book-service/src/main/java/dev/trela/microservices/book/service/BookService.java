package dev.trela.microservices.book.service;


import dev.trela.microservices.book.client.BookImageRestClient;
import dev.trela.microservices.book.model.Author;
import dev.trela.microservices.book.model.Book;
import dev.trela.microservices.book.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
@RequiredArgsConstructor
public class BookService {


    private final MessageService messageService;
    private final BookRepository bookRepository;
    private final AuthorService authorService;
    private final GenreService genreService;
    private final BookImageRestClient bookImageRestClient;




    public List<Book> getAllBooks(){
        return bookRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }

    public Book getBookById(int id){
        return bookRepository.findById(id).orElseThrow(()->
                new NoSuchElementException(messageService.getMessage("error.no.such.book")));
    }


    public Book addBook(Book book) throws IllegalArgumentException{
        book.setId(null);
        List<Author> resolvedAuthors = book.getAuthors().stream()
                        .map(author -> authorService.addAuthorIfNotExists(author.getName()))
                                .toList();

        book.setAuthors(resolvedAuthors);
        String genreName = book.getGenre().getName();
        book.setGenre(genreService.getGenreByName(genreName));
        return bookRepository.save(book);
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

    public String uploadBookCover(int bookId, InputStream inputStream, String filename){
        Book book = getBookById(bookId); // check if book exists
        String imageFileId = bookImageRestClient.uploadBookImage(inputStream,filename);
        book.setImageFileId(imageFileId);
        bookRepository.save(book);
        return imageFileId;
    }

    public ResponseEntity<byte[]> getBookCover(int bookId){
        Book book = getBookById(bookId);
        if(book.getImageFileId() == null || book.getImageFileId().isEmpty()){
            throw new NoSuchElementException(messageService.getMessage("error.no.book.cover"));
        }
        String bookCoverImageId = book.getImageFileId();
        return bookImageRestClient.getBookImage(bookCoverImageId);
    }



}
