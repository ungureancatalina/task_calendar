package com.example.taskuri.controller;

import com.example.taskuri.domain.TaskStatus;
import com.example.taskuri.domain.Taskss;
import com.example.taskuri.service.NoteService;
import com.example.taskuri.service.TaskService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TaskController {
    private TaskService taskService;
    private ObservableList<Taskss> taskList;
    private Long userId;
    private Taskss selectedTask;
    private NoteService noteService;
    private LocalDate selectedDate;

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @FXML
    private TableView<Taskss> taskTableView;
    @FXML
    private TableColumn<Taskss, String> titleColumn;
    @FXML
    private TableColumn<Taskss, LocalDateTime> startDateColumn;
    @FXML
    private TableColumn<Taskss, LocalDateTime> finishDateColumn;
    @FXML
    private TableColumn<Taskss, TaskStatus> statusColumn;

    @FXML
    private TextField taskTitleInput;
    @FXML
    private TextArea taskDescriptionInput;
    @FXML
    private DatePicker startDatePicker, finishDatePicker;
    @FXML
    private ComboBox<LocalTime> startTimeComboBox, finishTimeComboBox;
    @FXML
    private ComboBox<TaskStatus> statusComboBox;
    @FXML
    private Button modifyButton, deleteButton, seeNotesButton;
    @FXML
    private TextArea notesTextArea;

    public void setUserId(Long userId) {
        this.userId = userId;
        loadTasks();
    }

    public void setTaskService(TaskService taskService) {
        this.taskService = taskService;
        loadTasks();
    }

    public void setNoteService(NoteService noteService) {
        this.noteService = noteService;
    }

    public void setSelectedDate(LocalDate date) {
        this.selectedDate = date;
    }

    private void loadTasks() {
        if (taskService == null && userId == null) {
            showAlert("Error", "Task Service is not initialized and User ID is missing.");
            return;
        }
        List<Taskss> tasks = taskService.getTasksByUserId(userId);
        taskList = FXCollections.observableArrayList(tasks);
        taskTableView.setItems(taskList);
    }

    public void loadUserTasks(List<Taskss> tasks) {
        taskList.setAll(tasks);
        taskTableView.refresh();
    }


    @FXML
    public void initialize() {
        statusComboBox.getItems().addAll(TaskStatus.TO_DO, TaskStatus.IN_PROGRESS, TaskStatus.DONE);

        List<LocalTime> timeSlots = IntStream.range(0, 96)
                .mapToObj(i -> LocalTime.of(0, 0).plusMinutes(i * 15))
                .collect(Collectors.toList());

        startTimeComboBox.setItems(FXCollections.observableArrayList(timeSlots));
        finishTimeComboBox.setItems(FXCollections.observableArrayList(timeSlots));

        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        startDateColumn.setCellValueFactory(new PropertyValueFactory<>("startDateTime"));
        finishDateColumn.setCellValueFactory(new PropertyValueFactory<>("finishDateTime"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        startDateColumn.setCellFactory(column -> new TableCell<Taskss, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : item.format(dateTimeFormatter));
            }
        });

        finishDateColumn.setCellFactory(column -> new TableCell<Taskss, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : item.format(dateTimeFormatter));
            }
        });

        taskTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            selectedTask = newSelection;
            if (selectedTask != null) {
                fillTaskFields(selectedTask);
                modifyButton.setDisable(false);
                deleteButton.setDisable(false);
                seeNotesButton.setDisable(false);
            }
        });

        modifyButton.setDisable(true);
        deleteButton.setDisable(true);
        seeNotesButton.setDisable(true);
        notesTextArea.setText(
                "- To **add** a task, fill in all fields and click \"Add Task\".\n\n" +
                        "- To **modify** a task, select it from the table and update fields, then click \"Modify Task\".\n\n" +
                        "- To **delete** a task, select it from the table and click \"Delete Task\".\n\n" +
                        "- To **find more information** about the task, select it from the table and click \"See More\"."
        );
    }

    private void fillTaskFields(Taskss task) {
        taskTitleInput.setText(task.getTitle());
        taskDescriptionInput.setText(task.getDescription());
        startDatePicker.setValue(task.getStartDateTime().toLocalDate());
        startTimeComboBox.setValue(task.getStartDateTime().toLocalTime());

        if (task.getFinishDateTime() != null) {
            finishDatePicker.setValue(task.getFinishDateTime().toLocalDate());
            finishTimeComboBox.setValue(task.getFinishDateTime().toLocalTime());
        } else {
            finishDatePicker.setValue(null);
            finishTimeComboBox.setValue(null);
        }
        statusComboBox.setValue(task.getStatus());
    }

    @FXML
    public void addTask() {
        if (userId == null) {
            showAlert("Error", "No user logged in. Please restart the application.");
            return;
        }

        String title = taskTitleInput.getText().trim();
        String description = taskDescriptionInput.getText().trim();
        LocalDate startDate = startDatePicker.getValue();
        LocalTime startTime = startTimeComboBox.getValue();
        LocalDate finishDate = finishDatePicker.getValue();
        LocalTime finishTime = finishTimeComboBox.getValue();
        TaskStatus status = statusComboBox.getValue();

        if (title.isEmpty() || startDate == null || startTime == null || status == null) {
            showAlert("Error", "Please fill in all required fields.");
            return;
        }

        LocalDateTime startDateTime = LocalDateTime.of(startDate, startTime);
        LocalDateTime finishDateTime = (finishDate != null && finishTime != null) ? LocalDateTime.of(finishDate, finishTime) : null;

        if (finishDateTime != null && finishDateTime.isBefore(startDateTime)) {
            showAlert("Error", "Finish date must be after start date.");
            return;
        }

        List<Taskss> existingTasks = taskService.getTasksByUserId(userId);
        boolean taskExists = existingTasks.stream()
                .anyMatch(task -> task.getTitle().equalsIgnoreCase(title) &&
                        task.getStartDateTime().equals(startDateTime));

        if (taskExists) {
            showAlert("Error", "A task with the same title and start time already exists.");
            return;
        }

        Taskss newTask = new Taskss(null, title, description, startDateTime, finishDateTime, status, userId);
        taskService.addTask(newTask);
        clearFields();
    }

    @FXML
    public void deleteTask() {
        if (selectedTask == null) {
            showAlert("Warning", "No task selected!");
            return;
        }
        taskService.deleteTask(selectedTask.getId());
        taskList.remove(selectedTask);
        clearFields();
    }

    @FXML
    public void modifyTask() {
        if (selectedTask == null) {
            showAlert("Error", "No task selected for modification!");
            return;
        }

        String title = taskTitleInput.getText().trim();
        String description = taskDescriptionInput.getText().trim();
        LocalDate startDate = startDatePicker.getValue();
        LocalTime startTime = startTimeComboBox.getValue();
        LocalDate finishDate = finishDatePicker.getValue();
        LocalTime finishTime = finishTimeComboBox.getValue();
        TaskStatus status = statusComboBox.getValue();

        if (title.isEmpty() || startDate == null || startTime == null || status == null) {
            showAlert("Error", "Please fill in all required fields.");
            return;
        }

        LocalDateTime startDateTime = LocalDateTime.of(startDate, startTime);
        LocalDateTime finishDateTime = (finishDate != null && finishTime != null) ? LocalDateTime.of(finishDate, finishTime) : null;

        if (finishDateTime != null && finishDateTime.isBefore(startDateTime)) {
            showAlert("Error", "Finish date must be after start date.");
            return;
        }

        selectedTask.setTitle(title);
        selectedTask.setDescription(description);
        selectedTask.setStartDateTime(startDateTime);
        selectedTask.setFinishDateTime(finishDateTime);
        selectedTask.setStatus(status);

        taskService.updateTask(selectedTask);
        taskTableView.refresh();
    }

    @FXML
    public void openNotesview() {
        Taskss selectedTask = taskTableView.getSelectionModel().getSelectedItem();
        if (selectedTask == null) {
            showAlert("Error", "Please select a task first!");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/taskuri/note-view.fxml"));
            Parent root = loader.load();

            NoteController noteController = loader.getController();
            noteController.setNoteService(noteService);
            noteController.setTask(selectedTask);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Notes for Task: " + selectedTask.getTitle());
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    private void clearFields() {
        taskTitleInput.clear();
        taskDescriptionInput.clear();
        startDatePicker.setValue(null);
        finishDatePicker.setValue(null);
        startTimeComboBox.getSelectionModel().clearSelection();
        finishTimeComboBox.getSelectionModel().clearSelection();
        statusComboBox.getSelectionModel().clearSelection();
        selectedTask = null;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
