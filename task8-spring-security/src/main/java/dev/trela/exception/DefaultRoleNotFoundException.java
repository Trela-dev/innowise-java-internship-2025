package dev.trela.exception;

public class DefaultRoleNotFoundException extends RuntimeException {
    public DefaultRoleNotFoundException(String message) {
        super(message);
    }
}
