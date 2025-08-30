package dev.trela.microservices.image.dto;

public record ImageData(byte[] data, String contentType) {
}
