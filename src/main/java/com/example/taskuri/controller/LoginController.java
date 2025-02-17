package com.example.taskuri.controller;

import com.example.taskuri.domain.User;
import com.example.taskuri.service.NoteService;
import com.example.taskuri.service.TaskService;
import com.example.taskuri.service.UserService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {
    private UserService userService;
    private NoteService noteService;
    private TaskService taskService;
    private Long userId;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField passwordTextField;

    @FXML
    private Button loginButton, signupButton;
    @FXML
    private Button togglePasswordButton;

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setNoteService(NoteService noteService) {
        this.noteService = noteService;
    }

    public void setTaskService(TaskService taskService) {
        this.taskService = taskService;
    }

    @FXML
    public void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        User user = userService.getUserByEmail(email);

        if (user == null) {
            showAlert("Error", "User does not exist. Please sign up.");
            return;
        }
        if (!user.getPassword().equals(password)) {
            showAlert("Error", "Incorrect password. Please try again.");
            return;
        }
        this.userId = user.getId();
        openCalendarView();
    }

    @FXML
    public void handleSignup() {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        if (userService.getUserByEmail(email) != null) {
            showAlert("Error", "User already exists. Please log in.");
            return;
        }

        User newUser = new User(null, email, password);
        userService.addUser(newUser);
        User createdUser = userService.getUserByEmail(email);
        this.userId = createdUser.getId();

        showAlert("Success", "Account created successfully! Logging in...");
        openCalendarView();
    }

    private boolean isPasswordVisible = false;

    @FXML
    public void togglePasswordVisibility() {
        if (isPasswordVisible) {
            passwordField.setText(passwordTextField.getText());
            passwordField.setVisible(true);
            passwordTextField.setVisible(false);
        } else {
            passwordTextField.setText(passwordField.getText());
            passwordTextField.setVisible(true);
            passwordField.setVisible(false);
        }
        isPasswordVisible = !isPasswordVisible;
    }

    private void openCalendarView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/taskuri/calendar-view.fxml"));
            Parent root = loader.load();

            CalendarController calendarController = loader.getController();
            calendarController.setUserId(userId);
            calendarController.setTaskService(taskService);
            calendarController.setNoteService(noteService);

            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setTitle("Task Calendar");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
