package dev.trela.service;

import com.mongodb.client.gridfs.GridFSBucket;

import com.mongodb.client.gridfs.GridFSUploadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import com.mongodb.client.model.Filters;
import dev.trela.dto.ImageData;

import dev.trela.exception.ResourceNotFoundException;
import dev.trela.model.Author;
import dev.trela.model.Book;
import dev.trela.repository.BookRepository;

import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
@RequiredArgsConstructor
public class BookService {


    private final MessageService messageService;
    private final BookRepository bookRepository;
    private final GridFSBucket gridFSBucket;
    private final AuthorService authorService;
    private final GenreService genreService;


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


    public String uploadCoverImage(int bookId,MultipartFile file) throws IOException {
        String bookCoverImageFileId;
        Document metadata = new Document();
        metadata.put("contentType",file.getContentType());

        Book book = getBookById(bookId);

        if(book.getImageFileId() != null){
            gridFSBucket.delete(new ObjectId(book.getImageFileId()));
        }

        GridFSUploadOptions options = new GridFSUploadOptions().metadata(metadata);

        String fileName = file.getOriginalFilename();
        try (GridFSUploadStream uploadStream = gridFSBucket.openUploadStream(fileName,options)) {
            uploadStream.write(file.getBytes());
            ObjectId fileId = uploadStream.getObjectId();
            bookCoverImageFileId = fileId.toString();
            book.setImageFileId(bookCoverImageFileId);
            updateBook(bookId,book);
        }
        return bookCoverImageFileId;
    }

    public ImageData getCoverImage(int bookId) throws IOException{
        Book book = getBookById(bookId);
        String bookCoverImageFileId = book.getImageFileId();

        if(bookCoverImageFileId == null){
            throw new ResourceNotFoundException(messageService.getMessage("error.book.no.cover") + " "  + bookId);
        }

        ObjectId objectId = new ObjectId(bookCoverImageFileId);
        GridFSFile gridFSFile = gridFSBucket.find(Filters.eq("_id",objectId)).first();


        if(gridFSFile == null){
            throw new ResourceNotFoundException(messageService.getMessage("error.image.not.found") + " " + bookCoverImageFileId);
        }
        String contentType = "application/octet-stream";
        if(gridFSFile.getMetadata() != null && gridFSFile.getMetadata().getString("contentType") != null){
            contentType = gridFSFile.getMetadata().getString("contentType");
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        gridFSBucket.downloadToStream(objectId, outputStream);
        return new ImageData(outputStream.toByteArray(),contentType);
    }
}
