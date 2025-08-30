package dev.trela.microservices.book.client;

import lombok.extern.slf4j.Slf4j;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;


import java.io.InputStream;

@Slf4j
@Component
public class BookImageRestClient {

    private final RestClient restClient;

    public BookImageRestClient(@LoadBalanced RestClient.Builder builder) {
        this.restClient = builder
                .baseUrl("http://image-service")
                .build();
    }

    public String uploadBookImage(InputStream inputStream, String filename) {
        Resource fileResource = new MultipartInputStreamFileResource(inputStream, filename);
        LinkedMultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
        parts.add("coverImage", fileResource);

        return restClient.post()
                .uri("/api/book-cover")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(parts)
                .retrieve()
                .body(String.class);
    }

    public ResponseEntity<byte[]> getBookImage(String imageId) {
        return restClient.get()
                .uri("/api/book-cover/{id}", imageId)
                .accept(MediaType.ALL)
                .retrieve()
                .toEntity(byte[].class);
    }


}
