package com.example.taskuri.tests;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.taskuri.domain.User;
import com.example.taskuri.repository.Repository;
import com.example.taskuri.validation.Validator;
import com.example.taskuri.service.UserService;
import com.example.taskuri.validation.ValidationException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

class UserServiceTest {

    private UserService userService;

    @Mock
    private Repository<User> userRepository;

    @Mock
    private Validator<User> userValidator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserService(userRepository, userValidator);
    }

    @Test
    void addUser_ShouldValidateAndSaveUser() throws ValidationException {
        User user = new User(1L, "test@example.com", "password123");

        userService.addUser(user);

        verify(userValidator).validate(user);
        verify(userRepository).add(user);
    }

    @Test
    void getUserByEmail_ShouldReturnUser() {
        User user = new User(1L, "test@example.com", "password123");
        when(userRepository.getUserByEmail("test@example.com")).thenReturn(user);

        User retrievedUser = userService.getUserByEmail("test@example.com");

        assertNotNull(retrievedUser);
        assertEquals("test@example.com", retrievedUser.getEmail());
        verify(userRepository).getUserByEmail("test@example.com");
    }

    @Test
    void checkUserCredentials_ValidCredentials_ShouldReturnTrue() {
        User user = new User(1L, "test@example.com", "password123");
        when(userRepository.getUserByEmail("test@example.com")).thenReturn(user);

        assertTrue(userService.checkUserCredentials("test@example.com", "password123"));
    }

    @Test
    void checkUserCredentials_InvalidCredentials_ShouldReturnFalse() {
        when(userRepository.getUserByEmail("test@example.com")).thenReturn(null);

        assertFalse(userService.checkUserCredentials("test@example.com", "wrongpassword"));
    }

    @Test
    void authenticateUser_ValidCredentials_ShouldReturnUser() {
        User user = new User(1L, "test@example.com", "password123");
        when(userRepository.getUserByEmail("test@example.com")).thenReturn(user);

        Optional<User> authenticatedUser = userService.authenticateUser("test@example.com", "password123");

        assertTrue(authenticatedUser.isPresent());
        assertEquals("test@example.com", authenticatedUser.get().getEmail());
    }

    @Test
    void authenticateUser_InvalidCredentials_ShouldReturnEmpty() {
        when(userRepository.getUserByEmail("test@example.com")).thenReturn(null);

        Optional<User> authenticatedUser = userService.authenticateUser("test@example.com", "wrongpassword");

        assertTrue(authenticatedUser.isEmpty());
    }
}
