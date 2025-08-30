package dev.trela.microservices.auth.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import jakarta.validation.Validator;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserValidationTest {
    private Validator validator;

    @BeforeEach
    void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldFailValidationWhenUsernameIsBlank() {
        User user = new User();
        user.setUsername("   ");
        user.setPassword("validPassword123");
        user.setEmail("test@example.com");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals(1, violations.size());
        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals("{user.username.empty}", violation.getMessage()); // lub: "{user.username.empty}"
        assertEquals("username", violation.getPropertyPath().toString());
    }


    @Test
    void shouldFailValidationWhenPasswordTooShort() {
        User user = new User();
        user.setUsername("validUsername");
        user.setPassword("123"); // too short
        user.setEmail("test@example.com");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals(1, violations.size());
        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals("{user.password.too.short}", violation.getMessage());
        assertEquals("password", violation.getPropertyPath().toString());
    }


    @Test
    void shouldFailValidationWhenEmailIsNotValid() {
        User user = new User();
        user.setUsername("validUsername");
        user.setPassword("12345678");
        user.setEmail("test@ex@ample.com");// not valid

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals(1, violations.size());
        ConstraintViolation<User> violation = violations.iterator().next();
        assertEquals("{email.not.valid}", violation.getMessage());
        assertEquals("email", violation.getPropertyPath().toString());
    }








}
