package com.example.taskuri.tests;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.taskuri.controller.TaskController;
import com.example.taskuri.domain.TaskStatus;
import com.example.taskuri.domain.Taskss;
import com.example.taskuri.service.NoteService;
import com.example.taskuri.service.TaskService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

class TaskControllerTest {

    private TaskController taskController;

    @Mock
    private TaskService taskService;

    @Mock
    private NoteService noteService;

    @Mock
    private TableView<Taskss> taskTableView;

    @Mock
    private TableColumn<Taskss, String> titleColumn;

    @Mock
    private TableColumn<Taskss, LocalDateTime> startDateColumn;

    @Mock
    private TableColumn<Taskss, LocalDateTime> finishDateColumn;

    @Mock
    private TableColumn<Taskss, TaskStatus> statusColumn;

    @Mock
    private TextField taskTitleInput;

    @Mock
    private TextArea taskDescriptionInput;

    @Mock
    private DatePicker startDatePicker, finishDatePicker;

    @Mock
    private ComboBox<LocalTime> startTimeComboBox, finishTimeComboBox;

    @Mock
    private ComboBox<TaskStatus> statusComboBox;

    @Mock
    private Button modifyButton, deleteButton, seeNotesButton;

    private ObservableList<Taskss> taskList;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        taskController = new TaskController();
        taskController.setTaskService(taskService);
        taskController.setNoteService(noteService);
        taskList = FXCollections.observableArrayList();
    }

    @Test
    void addTask_Valid_ShouldCallServiceAndRefreshTable() {
        when(taskTitleInput.getText()).thenReturn("New Task");
        when(startDatePicker.getValue()).thenReturn(LocalDate.now());
        when(startTimeComboBox.getValue()).thenReturn(LocalTime.of(10, 0));
        when(statusComboBox.getValue()).thenReturn(TaskStatus.TO_DO);

        taskController.addTask();

        verify(taskService).addTask(any());
        verify(taskTableView).refresh();
    }

    @Test
    void addTask_Duplicate_ShouldShowError() {
        when(taskTitleInput.getText()).thenReturn("Duplicate Task");
        when(startDatePicker.getValue()).thenReturn(LocalDate.now());
        when(startTimeComboBox.getValue()).thenReturn(LocalTime.of(10, 0));
        when(statusComboBox.getValue()).thenReturn(TaskStatus.TO_DO);
        when(taskService.getTasksByUserId(anyLong())).thenReturn(List.of(
                new Taskss(1L, "Duplicate Task", "Description", LocalDateTime.now(), null, TaskStatus.TO_DO, 1L)
        ));

        taskController.addTask();
        verify(taskService, never()).addTask(any());
    }

    @Test
    void deleteTask_Valid_ShouldRemoveFromService() {
        Taskss task = new Taskss(1L, "Test Task", "Description", LocalDateTime.now(), null, TaskStatus.TO_DO, 1L);
        taskList.add(task);
        when(taskTableView.getSelectionModel().getSelectedItem()).thenReturn(task);

        taskController.deleteTask();
        verify(taskService).deleteTask(task.getId());
    }

    @Test
    void modifyTask_Valid_ShouldUpdateTask() {
        Taskss task = new Taskss(1L, "Original Title", "Description", LocalDateTime.now(), null, TaskStatus.TO_DO, 1L);
        when(taskTableView.getSelectionModel().getSelectedItem()).thenReturn(task);
        when(taskTitleInput.getText()).thenReturn("Modified Title");
        when(startDatePicker.getValue()).thenReturn(LocalDate.now());
        when(startTimeComboBox.getValue()).thenReturn(LocalTime.of(10, 0));
        when(statusComboBox.getValue()).thenReturn(TaskStatus.IN_PROGRESS);

        taskController.modifyTask();
        verify(taskService).updateTask(any());
    }

    @Test
    void openNotesView_Valid_ShouldOpenWindow() {
        Taskss task = new Taskss(1L, "Test Task", "Description", LocalDateTime.now(), null, TaskStatus.TO_DO, 1L);
        when(taskTableView.getSelectionModel().getSelectedItem()).thenReturn(task);

        taskController.openNotesview();
        verify(noteService, atLeastOnce());
    }
}
