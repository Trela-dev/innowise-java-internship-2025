package dev.trela.microservices.image.controller;

import dev.trela.microservices.image.dto.ImageData;
import dev.trela.microservices.image.service.BookCoverService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/book-cover")
@RequiredArgsConstructor
public class BookCoverController {

    private final BookCoverService bookCoverService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadCoverImage(
            @RequestParam("coverImage") MultipartFile file
    ) throws IOException {
        String fileId = bookCoverService.uploadCoverImage(file);
        return ResponseEntity.ok(fileId);
    }

    @GetMapping("/{fileId}")
    public ResponseEntity<byte[]> getCoverImage(@PathVariable String fileId) throws IOException {
        ImageData imageData = bookCoverService.getCoverImage(fileId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(imageData.contentType()));

        return new ResponseEntity<>(imageData.data(), headers, HttpStatus.OK);
    }

}
