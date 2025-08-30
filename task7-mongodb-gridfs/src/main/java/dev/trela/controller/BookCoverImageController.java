package dev.trela.controller;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.GridFSUploadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import dev.trela.exception.ResourceNotFoundException;
import dev.trela.model.Book;
import dev.trela.service.BookService;
import jakarta.servlet.http.HttpServletResponse;
import org.bson.types.ObjectId;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;

import static com.mongodb.client.model.Filters.eq;

@RestController
@RequestMapping("/api/books/{bookId}/cover")
public class BookCoverImageController {


    private final BookService bookService;
    private final GridFSBucket gridFSBucket;

    public BookCoverImageController(BookService bookService, GridFSBucket gridFSBucket) {
        this.bookService = bookService;
        this.gridFSBucket = gridFSBucket;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadImage(
            @PathVariable Integer bookId,
            @RequestParam("coverImage") MultipartFile file) throws IOException {

        Book book = bookService.getBookById(bookId);

        if(book.getImageFileId() != null){
            gridFSBucket.delete(new ObjectId(book.getImageFileId()));
        }

        String fileName = file.getOriginalFilename();
        try (GridFSUploadStream uploadStream = gridFSBucket.openUploadStream(fileName)) {
            uploadStream.write(file.getBytes());
            ObjectId fileId = uploadStream.getObjectId();
            book.setImageFileId(fileId.toString());
            bookService.updateBook(bookId,book);
        }
        return ResponseEntity.ok("Image uploaded successfully");
    }

    @GetMapping
    public ResponseEntity<StreamingResponseBody> downloadImage(
            @PathVariable Integer bookId,
            HttpServletResponse response
    ){

        Book book = bookService.getBookById(bookId);

        if(book.getImageFileId() == null){
            throw new ResourceNotFoundException("No image found for this book");
        }

        GridFSFile file = gridFSBucket.find(eq("_id", new ObjectId(book.getImageFileId()))).first();

        if (file == null){
            throw new ResourceNotFoundException("Image file not found");
        }

        String fileName = file.getFilename();

        if (fileName != null) {
            String lowered = fileName.toLowerCase();
            if (lowered.endsWith(".png")) {
                response.setContentType("image/png");
            } else if (lowered.endsWith(".jpg") || lowered.endsWith(".jpeg")) {
                response.setContentType("image/jpeg");
            } else {
                response.setContentType("application/octet-stream");
            }
        } else {
            response.setContentType("application/octet-stream");
        }

        response.setContentType("image/jpeg");
        response.setHeader("Content-Disposition",
                "attachment; filename=\"" + file.getFilename() + "\"");
        response.setCharacterEncoding("UTF-8");

        StreamingResponseBody stream = out -> {
            try(GridFSDownloadStream downloadStream = gridFSBucket.openDownloadStream(file.getObjectId()))
            {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while((bytesRead = downloadStream.read(buffer)) != -1){
                    out.write(buffer,0,bytesRead);
                }
            }
        };


        return ResponseEntity.ok().contentLength(file.getLength()).body(stream);



    }







}
