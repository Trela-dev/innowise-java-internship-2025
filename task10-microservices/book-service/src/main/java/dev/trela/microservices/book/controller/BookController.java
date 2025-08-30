package dev.trela.microservices.book.controller;


import dev.trela.microservices.book.model.Book;
import dev.trela.microservices.book.service.BookService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {
//    GET    /api/books          - Get all books
//    GET    /api/books/{id}     - Get a specific book
//    POST   /api/books          - Create a new book
//    PUT    /api/books/{id}     - Update a book
//    DELETE /api/books/{id}     - Delete a book
//    GET    /api/books/search   - Search books by keyword
//
    private final BookService bookService;


    @GetMapping
    public List<Book> getAllBooks() {
        return bookService.getAllBooks();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable int id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    @PostMapping
    public ResponseEntity<Book> createBook(@RequestBody Book book) {
        Book created = bookService.addBook(book);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable int id, @RequestBody Book book) {
        Book updatedBook  = bookService.updateBook(id,book);
        return ResponseEntity.ok(updatedBook);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable int id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public List<Book> searchBooks(@RequestParam String keyword) {
        return bookService.searchByKeyword(keyword);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @CircuitBreaker(name = "imageService", fallbackMethod = "fallbackCover")
    @PostMapping("/{id}/cover")
    public ResponseEntity<String> uploadBookImage(@PathVariable int id, @RequestParam("coverImage") MultipartFile file) throws IOException {
        String fileId = bookService.uploadBookCover(id,file.getInputStream(),file.getOriginalFilename());
        return ResponseEntity.status(HttpStatus.CREATED).body(fileId);
    }

    @CircuitBreaker(name = "imageService",
            fallbackMethod = "fallbackCover")
    @GetMapping("/{id}/cover")
    public ResponseEntity<byte[]> getBookImage(@PathVariable int id){
        return bookService.getBookCover(id);
    }


    public ResponseEntity<String> fallbackCover(int id, MultipartFile file, Throwable t) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Image service unavailable. Try again later.");
    }
    public ResponseEntity<String> fallbackCover(int id,  Throwable t) {
        if(t instanceof NoSuchElementException){
            throw new NoSuchElementException(t.getMessage());
        }
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Image service unavailable. Try again later.");
    }


}
