package dev.trela.microservices.book.exception;

public class JWTDecodingException extends RuntimeException {
    public JWTDecodingException(String message) {
        super(message);
    }
}
