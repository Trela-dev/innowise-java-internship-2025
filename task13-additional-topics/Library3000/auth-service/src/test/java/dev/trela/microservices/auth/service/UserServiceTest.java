package dev.trela.microservices.auth.service;


import dev.trela.microservices.auth.exception.UsernameAlreadyInUseException;
import dev.trela.microservices.auth.model.User;
import dev.trela.microservices.auth.model.Role;
import dev.trela.microservices.auth.repository.RoleRepository;
import dev.trela.microservices.auth.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private MessageService messageService;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldThrowWhenUsernameAlreadyExists() {
        String username = "existingUser";
        String password = "validPassword123";
        String email = "test@gmail.com";
        Role role = new Role("ROLE_USER");

        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(role));
        when(passwordEncoder.encode(password)).thenReturn("encodedPassword");
        when(messageService.getMessage("user.username.duplicate"))
                .thenReturn("Username 'existingUser' is already in use");

        when(userRepository.findByUsername(username)).
                thenReturn(Optional.of(new User(username, "encodedPassword", role, email)));

        UsernameAlreadyInUseException exception = assertThrows(
                UsernameAlreadyInUseException.class,
                () -> userService.registerUser(username, password, email)
        );

        assertEquals("Username 'existingUser' is already in use", exception.getMessage());
        verify(messageService).getMessage("user.username.duplicate");
    }







}
