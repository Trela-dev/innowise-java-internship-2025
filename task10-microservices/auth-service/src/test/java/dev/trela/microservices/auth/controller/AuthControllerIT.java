package dev.trela.microservices.auth.controller;

import dev.trela.microservices.auth.config.SecurityConfig;
import dev.trela.microservices.auth.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import dev.trela.microservices.auth.TestcontainersConfiguration;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
public class AuthControllerIT{

    @LocalServerPort
    int port;


    @Autowired
    TestRestTemplate restTemplate;



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
