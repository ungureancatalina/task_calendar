package com.example.taskuri.controller;

import com.example.taskuri.domain.Taskss;
import com.example.taskuri.service.NoteService;
import com.example.taskuri.service.TaskService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class CalendarController {
    @FXML
    private GridPane calendarGrid;

    @FXML
    private Label monthLabel;

    @FXML
    private Button prevMonthButton, nextMonthButton;
    @FXML
    private Label taskStatusLabel;

    private YearMonth currentMonth;
    private TaskService taskService;
    private NoteService noteService;
    private Long userId;

    public void setNoteService(NoteService noteService) {
        this.noteService = noteService;
    }

    public void setTaskService(TaskService taskService) {
        this.taskService = taskService;
        checkAndUpdateCalendar();
    }

    public void setUserId(Long userId) {
        this.userId = userId;
        checkAndUpdateCalendar();
    }

    @FXML
    public void initialize() {
        currentMonth = YearMonth.now();
    }

    private void checkAndUpdateCalendar() {
        if (taskService != null && userId != null) {
            updateCalendar();
        }
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

    public YearMonth getCurrentMonth() {
        return currentMonth;
    }


    public void updateCalendar() {
        if (taskService == null || userId == null) {
            return;
        }

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
        if (dayOfWeek == 7) dayOfWeek = 0;

        int row = 1;
        int column = dayOfWeek;
        int daysInMonth = currentMonth.lengthOfMonth();
        LocalDate today = LocalDate.now();

        for (int i = 0; i < column; i++) {
            StackPane emptyPane = createEmptyCell();
            calendarGrid.add(emptyPane, i, row);
        }

        List<LocalDate> taskDates = taskService.getTasksByUserId(userId)
                .stream()
                .map(task -> task.getStartDateTime().toLocalDate())
                .distinct()
                .toList();

        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate currentDate = currentMonth.atDay(day);
            Label dayLabel = new Label(String.valueOf(day));
            dayLabel.setFont(new Font("Arial", 20));
            dayLabel.setTextAlignment(TextAlignment.CENTER);
            dayLabel.setMaxWidth(Double.MAX_VALUE);
            dayLabel.setMaxHeight(Double.MAX_VALUE);
            String defaultStyle = "-fx-alignment: CENTER; -fx-border-color: black; -fx-text-fill: white; -fx-background-color: #7a5dab; -fx-padding: 15px;";

            if (currentDate.equals(today)) {
                defaultStyle = "-fx-alignment: CENTER; -fx-border-color: black; -fx-text-fill: black; -fx-background-color: #a48bce; -fx-padding: 15px;";
            }
            if (taskDates.contains(currentDate)) {
                defaultStyle = "-fx-alignment: CENTER; -fx-border-color: black; -fx-text-fill: red; -fx-background-color: #7a5dab; -fx-padding: 15px;";
            }
            if (currentDate.equals(today) && taskDates.contains(currentDate)) {
                defaultStyle = "-fx-alignment: CENTER; -fx-border-color: black; -fx-text-fill: red; -fx-background-color: #a48bce; -fx-padding: 15px;";
            }

            dayLabel.setStyle(defaultStyle);
            String finalDefaultStyle = defaultStyle;
            dayLabel.setOnMouseEntered(event -> dayLabel.setStyle("-fx-alignment: CENTER;-fx-background-color: #6a449b; -fx-border-color: black; -fx-text-fill: white; -fx-padding: 15px;"));
            dayLabel.setOnMouseExited(event -> dayLabel.setStyle(finalDefaultStyle));


            int finalDay = day;
            dayLabel.setOnMouseClicked(event -> openTaskView(currentMonth.atDay(finalDay)));

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
        updateTaskStatusForMonth();
    }

    private void updateTaskStatusForMonth() {
        LocalDate today = LocalDate.now();
        YearMonth displayedMonth = currentMonth;

        List<Taskss> allTasks = taskService.getTasksByUserId(userId);

        List<Taskss> pendingTasks = allTasks.stream()
                .filter(task -> !task.getStatus().name().equals("DONE"))
                .toList();

        if (displayedMonth.equals(YearMonth.from(today))) {
            boolean hasTasksToday = pendingTasks.stream()
                    .anyMatch(task -> task.getStartDateTime().toLocalDate().equals(today));

            Optional<LocalDate> closestFutureTaskDate = pendingTasks.stream()
                    .map(task -> task.getStartDateTime().toLocalDate())
                    .filter(date -> date.isAfter(today))
                    .min(LocalDate::compareTo);

            if (hasTasksToday) {
                taskStatusLabel.setText("Check today's tasks, you have something going on!");
            } else if (closestFutureTaskDate.isPresent()) {
                taskStatusLabel.setText("You have tasks to do until " + closestFutureTaskDate.get());
            } else {
                taskStatusLabel.setText("No pending tasks. Enjoy your day!");
            }
        } else {
            Optional<LocalDate> earliestTaskDate = pendingTasks.stream()
                    .map(task -> task.getStartDateTime().toLocalDate())
                    .filter(date -> YearMonth.from(date).equals(displayedMonth))
                    .min(LocalDate::compareTo);

            if (earliestTaskDate.isPresent()) {
                taskStatusLabel.setText("First task in " + displayedMonth.getMonth() + ": " + earliestTaskDate.get());
            } else {
                taskStatusLabel.setText("No tasks scheduled for " + displayedMonth.getMonth() + ".");
            }
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
        if (taskService == null) {
            showAlert("Error", "Task Service is not initialized.");
            return;
        }
        if (userId == null) {
            showAlert("Error", "User ID is missing.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/taskuri/task-view.fxml"));
            Parent root = loader.load();

            TaskController taskController = loader.getController();
            taskController.setTaskService(taskService);
            taskController.setUserId(userId);
            taskController.setSelectedDate(date);

            List<Taskss> userTasks = taskService.getTasksByUserAndDate(userId, date);
            if (userTasks != null) {
                taskController.loadUserTasks(userTasks);
            }

            if (noteService != null) {
                taskController.setNoteService(noteService);
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

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
