package com.example.taskuri.controller;

import com.example.taskuri.service.NoteService;
import com.example.taskuri.service.TaskService;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import javafx.fxml.FXMLLoader;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.geometry.Pos;


import java.util.Locale;

public class CalendarController {

    @FXML
    private GridPane calendarGrid;

    @FXML
    private Label monthLabel;

    @FXML
    private Button prevMonthButton, nextMonthButton;

    private YearMonth currentMonth;
    private TaskService taskService;
    private NoteService noteService;

    public void setNoteService(NoteService noteService) {
        this.noteService = noteService;
    }


    public void setTaskService(TaskService taskService) {
        this.taskService = taskService;
    }

    @FXML
    public void initialize() {
        currentMonth = YearMonth.now();
        updateCalendar();
    }

    @FXML
    public void goToPreviousMonth() {
        currentMonth = currentMonth.minusMonths(1);
        updateCalendar();
    }

    @FXML
    public void goToNextMonth() {
        currentMonth = currentMonth.plusMonths(1);
        updateCalendar();
    }


    private void updateCalendar() {
        calendarGrid.getChildren().clear();
        monthLabel.setText(currentMonth.getMonth().getDisplayName(java.time.format.TextStyle.FULL, Locale.getDefault()) + " " + currentMonth.getYear());

        String[] weekDays = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        for (int col = 0; col < 7; col++) {
            Label dayLabel = new Label(weekDays[col]);
            dayLabel.setFont(new Font("Arial", 18));
            dayLabel.setTextAlignment(TextAlignment.CENTER);
            dayLabel.setStyle("-fx-alignment: CENTER; -fx-font-weight: bold; -fx-background-color: #7a5dab; -fx-text-fill: white; -fx-padding: 10px; -fx-border-color: black;");
            dayLabel.setMaxWidth(Double.MAX_VALUE);
            dayLabel.setMaxHeight(Double.MAX_VALUE);
            calendarGrid.add(dayLabel, col, 0);
        }

        LocalDate firstDayOfMonth = currentMonth.atDay(1);
        int dayOfWeek = firstDayOfMonth.getDayOfWeek().getValue();

        int row = 1;
        int column = dayOfWeek - 1;
        int daysInMonth = currentMonth.lengthOfMonth();

        for (int i = 0; i < column; i++) {
            StackPane emptyPane = createEmptyCell();
            calendarGrid.add(emptyPane, i, row);
        }

        for (int day = 1; day <= daysInMonth; day++) {
            Label dayLabel = new Label(String.valueOf(day));
            dayLabel.setFont(new Font("Arial", 20));
            dayLabel.setTextAlignment(TextAlignment.CENTER);
            dayLabel.setStyle("-fx-alignment: CENTER; -fx-border-color: black; -fx-text-fill: white; -fx-background-color: #7a5dab; -fx-padding: 15px;");
            int finalDay = day;
            dayLabel.setOnMouseClicked(event -> openTaskView(currentMonth.atDay(finalDay)));
            dayLabel.setMaxWidth(Double.MAX_VALUE);
            dayLabel.setMaxHeight(Double.MAX_VALUE);
            dayLabel.setOnMouseEntered(event -> dayLabel.setStyle("-fx-alignment: CENTER;-fx-background-color: #6a449b; -fx-border-color: black; -fx-text-fill: white; -fx-padding: 15px;"));
            dayLabel.setOnMouseExited(event -> dayLabel.setStyle("-fx-alignment: CENTER;-fx-background-color: #7a5dab; -fx-border-color: black; -fx-text-fill: white; -fx-padding: 15px;"));

            StackPane pane = new StackPane(dayLabel);
            calendarGrid.add(pane, column, row);

            column++;
            if (column == 7) {
                column = 0;
                row++;
            }
        }

        while (column < 7 && column > 0) {
            StackPane emptyPane = createEmptyCell();
            calendarGrid.add(emptyPane, column, row);
            column++;
        }
    }

    private StackPane createEmptyCell() {
        Label emptyLabel = new Label();
        emptyLabel.setStyle("-fx-border-color: black; -fx-background-color: #a48bce;");
        emptyLabel.setMaxWidth(Double.MAX_VALUE);
        emptyLabel.setMaxHeight(Double.MAX_VALUE);

        StackPane emptyPane = new StackPane(emptyLabel);
        emptyPane.setAlignment(Pos.CENTER);
        return emptyPane;
    }

    private void openTaskView(LocalDate date) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/taskuri/task-view.fxml"));
            Parent root = loader.load();

            TaskController taskController = loader.getController();
            taskController.setTaskService(taskService);
            taskController.setSelectedDate(date);

            if (this.noteService != null) {
                taskController.setNoteService(this.noteService);
            }

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Tasks for " + date);
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
