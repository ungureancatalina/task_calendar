package com.example.taskuri;

import com.example.taskuri.controller.CalendarController;
import com.example.taskuri.repository.NoteRepositoryImpl;
import com.example.taskuri.repository.TaskRepositoryImpl;
import com.example.taskuri.service.NoteService;
import com.example.taskuri.service.TaskService;
import com.example.taskuri.validation.NotesValidator;
import com.example.taskuri.validation.TaskValidator;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class HelloApplication extends Application {
    private static final String URL = "jdbc:postgresql://localhost:5432/taskuri";
    private static final String USER = "postgres";
    private static final String PASSWORD = "catalina";

    @Override
    public void start(Stage stage) throws IOException {
        checkDatabaseConnection();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/taskuri/calendar-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 980, 650);

        CalendarController controller = fxmlLoader.getController();
        NoteService noteService = new NoteService(new NoteRepositoryImpl(URL, USER, PASSWORD), new NotesValidator());
        TaskService taskService = new TaskService(new TaskRepositoryImpl(URL, USER, PASSWORD), new TaskValidator());
        controller.setTaskService(taskService);
        controller.setNoteService(noteService);

        stage.setTitle("Task Calendar");
        stage.setScene(scene);
        stage.show();
    }

    private void checkDatabaseConnection() {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            System.out.println("Database connection established");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Connection", "Connection failed: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch();
    }
}
