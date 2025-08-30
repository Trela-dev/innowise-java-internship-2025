package dev.trela.controller;

import dev.trela.config.security.FilterChainConfig;
import dev.trela.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Import(FilterChainConfig.class)
public class AuthControllerIT extends IntegrationTestBase{
    @Test
    void shouldRegisterUserSuccessfully(){
        User user = new User("john_doe","password123",null);

        ResponseEntity<String> response =
                restTemplate.postForEntity(
                        "http://localhost:" + port + "/api/auth/register",
                        user,
                        String.class
                );

        assertEquals(HttpStatus.OK,response.getStatusCode());

    }

    @Test
    void shouldFailWhenUsernameIsEmpty(){
        User user = new User("","password123",null);

        ResponseEntity<String> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/auth/register",
                user,
                String.class
        );

        assertTrue(response.getStatusCode().is4xxClientError());
    }


}
