package dev.trela.controller;


import dev.trela.dto.ImageData;

import dev.trela.service.BookService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/books/{bookId}/cover")
@RequiredArgsConstructor
public class BookCoverImageController {

    private final BookService bookCoverService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadCoverImage(
            @PathVariable Integer bookId,
            @RequestParam("coverImage") MultipartFile file
    ) throws IOException {
        String fileId = bookCoverService.uploadCoverImage(bookId, file);
        return ResponseEntity.ok(fileId);
    }

    @GetMapping
    public ResponseEntity<byte[]> getCoverImage(@PathVariable int bookId) throws IOException {
        ImageData imageData = bookCoverService.getCoverImage(bookId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(imageData.contentType()));
        return new ResponseEntity<>(imageData.data(), headers, HttpStatus.OK);
    }

}
