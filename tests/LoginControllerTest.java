package com.example.taskuri.tests;

import static org.mockito.Mockito.*;

import com.example.taskuri.controller.LoginController;
import com.example.taskuri.domain.User;
import com.example.taskuri.service.NoteService;
import com.example.taskuri.service.TaskService;
import com.example.taskuri.service.UserService;
import javafx.scene.control.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class LoginControllerTest {

    private LoginController loginController;

    @Mock
    private UserService userService;

    @Mock
    private TaskService taskService;

    @Mock
    private NoteService noteService;

    @Mock
    private TextField emailField, passwordTextField;

    @Mock
    private PasswordField passwordField;

    @Mock
    private Button loginButton, signupButton;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        loginController = new LoginController();
        loginController.setUserService(userService);
        loginController.setTaskService(taskService);
        loginController.setNoteService(noteService);
    }

    @Test
    void handleLogin_ValidUser_ShouldOpenCalendar() {
        when(emailField.getText()).thenReturn("test@example.com");
        when(passwordField.getText()).thenReturn("password");
        when(userService.getUserByEmail("test@example.com")).thenReturn(new User(1L, "test@example.com", "password"));

        loginController.handleLogin();
        verify(userService).getUserByEmail("test@example.com");
    }

    @Test
    void handleLogin_InvalidPassword_ShouldShowError() {
        when(emailField.getText()).thenReturn("test@example.com");
        when(passwordField.getText()).thenReturn("wrongpass");
        when(userService.getUserByEmail("test@example.com")).thenReturn(new User(1L, "test@example.com", "password"));

        loginController.handleLogin();
        verify(userService).getUserByEmail("test@example.com");
    }

    @Test
    void handleSignup_NewUser_ShouldCreateAccount() {
        when(emailField.getText()).thenReturn("newuser@example.com");
        when(passwordField.getText()).thenReturn("password");
        when(userService.getUserByEmail("newuser@example.com")).thenReturn(null);

        loginController.handleSignup();
        verify(userService).addUser(any(User.class));
    }
}
