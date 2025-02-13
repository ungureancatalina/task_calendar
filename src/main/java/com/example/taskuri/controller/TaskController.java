package com.example.taskuri.controller;

import com.example.taskuri.domain.TaskStatus;
import com.example.taskuri.domain.Taskss;
import com.example.taskuri.service.NoteService;
import com.example.taskuri.service.TaskService;
import com.example.taskuri.validation.ValidationException;
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
    private LocalDate selectedDate;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private NoteService noteService;

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
    private Button modifyButton, deleteButton, seeNotes;

    private Taskss selectedTask;

    @FXML
    private TextArea notesTextArea;



    public void setTaskService(TaskService taskService) {
        this.taskService = taskService;
        loadTasks();
    }

    private void loadTasks() {
        if (taskService == null) {
            showAlert("Error", "Service is not set.");
            return;
        }
        List<Taskss> tasks = taskService.getAllTasks();
        taskList = FXCollections.observableArrayList(tasks);
        taskTableView.setItems(taskList);
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
        startDateColumn.setCellFactory(column -> new TableCell<Taskss, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : item.format(dateTimeFormatter));
            }
        });

        finishDateColumn.setCellValueFactory(new PropertyValueFactory<>("finishDateTime"));
        finishDateColumn.setCellFactory(column -> new TableCell<Taskss, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : item.format(dateTimeFormatter));
            }
        });
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        taskTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                fillTaskFields(newSelection);
                modifyButton.setDisable(false);
                deleteButton.setDisable(false);
                seeNotes.setDisable(false);
            }
        });


        taskTableView.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Taskss task, boolean empty) {
                super.updateItem(task, empty);
                if (empty || task == null) {
                    setStyle("");
                } else {
                    setStyle("-fx-background-color: #c7a0e8; -fx-text-fill: white;");
                }
            }
        });

        modifyButton.setDisable(true);
        deleteButton.setDisable(true);
        seeNotes.setDisable(true);

        startDatePicker.getEditor().setStyle("-fx-background-color: #a48bce; -fx-prompt-text-fill: black;");
        finishDatePicker.getEditor().setStyle("-fx-background-color: #a48bce; -fx-prompt-text-fill: black;");

        notesTextArea.setText(
                "- To **add** a task**, fill in all fields and click \"Add Task\".\n\n" +
                        "- To **modify** a task**, select it from the table and update fields, then click \"Modify Task\".\n\n" +
                        "- To **delete** a task**, select it from the table and click \"Delete Task\".\n\n" +
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
        String title = taskTitleInput.getText();
        String description = taskDescriptionInput.getText();
        LocalDate startDate = startDatePicker.getValue();
        LocalDate finishDate = finishDatePicker.getValue();
        LocalTime startTime = startTimeComboBox.getValue();
        LocalTime finishTime = finishTimeComboBox.getValue();
        TaskStatus status = statusComboBox.getValue();

        if (title.isEmpty() || description.isEmpty() || startDate == null || startTime == null || status == null) {
            showAlert("Error", "Please fill in all required fields!");
            return;
        }

        LocalDateTime startDateTime = LocalDateTime.of(startDate, startTime);
        LocalDateTime finishDateTime = (finishDate != null && finishTime != null) ? LocalDateTime.of(finishDate, finishTime) : null;

        if (finishDateTime != null && finishDateTime.isBefore(startDateTime)) {
            showAlert("Error", "Finish date and time must be after Start date and time.");
            return;
        }

        Taskss newTask = new Taskss(null, title, description, startDateTime, finishDateTime, status);
        taskService.addTask(newTask);
        taskList.add(newTask);

        clearFields();
    }

    @FXML
    public void modifyTask() {
        if (selectedTask == null) {
            showAlert("Error", "No task selected for modification!");
            return;
        }

        selectedTask.setTitle(taskTitleInput.getText());
        selectedTask.setDescription(taskDescriptionInput.getText());
        selectedTask.setStartDateTime(LocalDateTime.of(startDatePicker.getValue(), startTimeComboBox.getValue()));

        if (finishDatePicker.getValue() != null && finishTimeComboBox.getValue() != null) {
            selectedTask.setFinishDateTime(LocalDateTime.of(finishDatePicker.getValue(), finishTimeComboBox.getValue()));
            if (selectedTask.getFinishDateTime().isBefore(selectedTask.getStartDateTime())) {
                showAlert("Error", "Finish date and time must be after Start date and time.");
                return;
            }
        } else {
            selectedTask.setFinishDateTime(null);
        }

        selectedTask.setStatus(statusComboBox.getValue());

        taskService.updateTask(selectedTask);
        taskTableView.refresh();
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

    public void setSelectedDate(LocalDate date) {
        this.selectedDate = date;
        loadTasksForDate();
    }

    private void loadTasksForDate() {
        if (selectedDate == null || taskService == null) {
            showAlert("Error", "No date or service set");
            return;
        }

        List<Taskss> tasks = taskService.getTasksByStartDate(selectedDate);
        taskList.setAll(tasks);
    }

    public void setNoteService(NoteService noteService) {
        this.noteService = noteService;
    }

    @FXML
    public void notespage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/taskuri/notes-view.fxml"));
            Parent root = loader.load();

            NoteController noteController = loader.getController();

            if (this.noteService != null) {
                noteController.setNoteService(this.noteService);
            } else {
                showAlert("Error", "NoteService is not initialized.");
                return;
            }

            Stage stage = new Stage();
            stage.setTitle("Task Notes");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
