package com.example.taskuri.controller;

import com.example.taskuri.domain.User;
import com.example.taskuri.service.UserService;
import com.example.taskuri.validation.ValidationException;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.util.List;

public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    public void addUser(User user) {
        try {
            userService.addUser(user);
        } catch (ValidationException e) {
            showAlert(e.getMessage());
        }
    }

    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    public User getUserByEmail(String email) {
        return userService.getUserByEmail(email);
    }


    private void showAlert(String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Validation Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
