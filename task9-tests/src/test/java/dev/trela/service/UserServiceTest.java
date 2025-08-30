package dev.trela.service;

import dev.trela.exception.UsernameAlreadyInUseException;
import dev.trela.model.Role;
import dev.trela.model.User;
import dev.trela.repository.RoleRepository;
import dev.trela.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
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
    void shouldThrowWhenUsernameIsEmpty(){
        when(messageService.getMessage("user.username.empty")).thenReturn("Username cannot be empty");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.registerUser("   ", "validPassword123")
        );

        assertEquals("Username cannot be empty", exception.getMessage());
        verify(messageService).getMessage("user.username.empty");
        verifyNoInteractions(userRepository);
    }

    @Test
    void shouldThrowWhenPasswordTooShort() {

        when(messageService.getMessage("user.password.too.short"))
                .thenReturn("Password must be at least 8 characters long");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.registerUser("validUsername", "123") // hasło za krótkie
        );

        assertEquals("Password must be at least 8 characters long", exception.getMessage());
        verify(messageService).getMessage("user.password.too.short");
        verifyNoInteractions(userRepository);
    }

    @Test
    void shouldThrowWhenUsernameAlreadyExists() {
        String username = "existingUser";
        String password = "validPassword123";
        Role role = new Role("ROLE_USER");

        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(role));
        when(passwordEncoder.encode(password)).thenReturn("encodedPassword");
        when(messageService.getMessage("user.username.duplicate", username))
                .thenReturn("Username 'existingUser' is already in use");

        when(userRepository.save(any(User.class)))
                .thenThrow(new DataIntegrityViolationException("duplicate key"));

        UsernameAlreadyInUseException exception = assertThrows(
                UsernameAlreadyInUseException.class,
                () -> userService.registerUser(username, password)
        );

        assertEquals("Username 'existingUser' is already in use", exception.getMessage());
        verify(userRepository).save(any(User.class));
        verify(messageService).getMessage("user.username.duplicate", username);

    }






}
