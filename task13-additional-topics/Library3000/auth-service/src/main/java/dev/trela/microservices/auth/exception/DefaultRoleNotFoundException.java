package dev.trela.microservices.auth.exception;

public class DefaultRoleNotFoundException extends RuntimeException {
    public DefaultRoleNotFoundException(String message) {
        super(message);
    }
}
