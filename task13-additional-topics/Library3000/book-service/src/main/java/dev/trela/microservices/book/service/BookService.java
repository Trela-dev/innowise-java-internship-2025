package dev.trela.microservices.book.service;


import com.ecwid.consul.v1.agent.model.Self;
import dev.trela.microservices.book.client.BookImageRestClient;
import dev.trela.microservices.book.dto.BookDTO;
import dev.trela.microservices.book.model.Author;
import dev.trela.microservices.book.model.Book;
import dev.trela.microservices.book.repository.BookRepository;

import dev.trela.microservices.book.service.mapper.BookMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
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




    public List<BookDTO> getAllBooks(){
        List<Book> books = bookRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
        return books.stream()
                .map(BookMapper::toDTO)
                .toList();
    }

    @Cacheable(value = "books", key = "#id")
    public BookDTO getBookById(int id){
        Book foundBook = getBookEntity(id);
        return BookMapper.toDTO(foundBook);
    }


    @CacheEvict(value = "books", allEntries = true)
    public BookDTO addBook(Book book) throws IllegalArgumentException{
        book.setId(null);
        List<Author> resolvedAuthors = book.getAuthors().stream()
                        .map(author -> authorService.addAuthorIfNotExists(author.getName()))
                                .toList();

        book.setAuthors(resolvedAuthors);
        String genreName = book.getGenre().getName();
        book.setGenre(genreService.getGenreByName(genreName));

        Book savedBook = bookRepository.save(book);

        return BookMapper.toDTO(savedBook);
    }

    @CachePut(value = "books", key= "#id")
    public BookDTO updateBook(Integer id, Book updatedBook) throws NoSuchElementException{
        Book existingBook = getBookEntity(id);
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

        Book updatedBookEntity = bookRepository.save(existingBook);

       return BookMapper.toDTO(updatedBookEntity);
    }

    @CacheEvict(value = "books", key = "#bookId")
    public void deleteBook(int bookId) throws NoSuchElementException{
        getBookEntity(bookId);
        bookRepository.deleteById(bookId);
    }

    public List<BookDTO> searchByKeyword(String keyword){
        List<Book> foundBook = bookRepository.searchByKeyword(keyword);
        return  foundBook.stream()
                .map(BookMapper::toDTO)
                .toList();
    }
    @CachePut(value = "books", key ="#bookId")
    public BookDTO uploadBookCover(int bookId, InputStream inputStream, String filename){
        Book book =   getBookEntity(bookId); // check if book exists
        String imageFileId = bookImageRestClient.uploadBookImage(inputStream, filename);
        book.setImageFileId(imageFileId);
        Book updatedBook = bookRepository.save(book);
        return BookMapper.toDTO(updatedBook);
    }

    public ResponseEntity<byte[]> getBookCover(int bookId){
        Book book =  getBookEntity(bookId);
        if(book.getImageFileId() == null || book.getImageFileId().isEmpty()){
            throw new NoSuchElementException(messageService.getMessage("error.no.book.cover"));
        }
        String bookCoverImageId = book.getImageFileId();
        return bookImageRestClient.getBookImage(bookCoverImageId);
    }

    private Book getBookEntity(int id) {
        return bookRepository.findById(id).orElseThrow(() ->
                new NoSuchElementException(messageService.getMessage("error.no.such.book")));
    }



}
