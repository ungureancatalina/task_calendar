package com.example.taskuri.tests;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.taskuri.controller.CalendarController;
import com.example.taskuri.domain.Taskss;
import com.example.taskuri.service.NoteService;
import com.example.taskuri.service.TaskService;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

class CalendarControllerTest {

    private CalendarController calendarController;

    @Mock
    private TaskService taskService;

    @Mock
    private NoteService noteService;

    @Mock
    private GridPane calendarGrid;

    @Mock
    private Label monthLabel, taskStatusLabel;

    @Mock
    private Button prevMonthButton, nextMonthButton;

    private Long userId = 1L;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        calendarController = new CalendarController();
        calendarController.setTaskService(taskService);
        calendarController.setNoteService(noteService);
        calendarController.setUserId(userId);
    }

    @Test
    void testGoToPreviousMonth() {
        YearMonth currentMonth = YearMonth.now();
        calendarController.goToPreviousMonth();
        YearMonth expectedMonth = currentMonth.minusMonths(1);
        assertEquals(expectedMonth, calendarController.getCurrentMonth());
    }

    @Test
    void testGoToNextMonth() {
        YearMonth currentMonth = YearMonth.now();
        calendarController.goToNextMonth();
        YearMonth expectedMonth = currentMonth.plusMonths(1);
        assertEquals(expectedMonth, calendarController.getCurrentMonth());
    }

    @Test
    void testUpdateCalendar_NoTasks() {
        when(taskService.getTasksByUserId(userId)).thenReturn(List.of());

        calendarController.updateCalendar();

        verify(calendarGrid, atLeastOnce()).getChildren().clear();
        verify(monthLabel, atLeastOnce()).setText(anyString());
        verify(taskStatusLabel, atLeastOnce()).setText("No pending tasks. Enjoy your day!");
    }

    @Test
    void testUpdateCalendar_WithTasksToday() {
        LocalDate today = LocalDate.now();
        Taskss task = new Taskss(1L, "Test Task", "Description", LocalDateTime.of(today, LocalDateTime.now().toLocalTime()), null, null, userId);

        when(taskService.getTasksByUserId(userId)).thenReturn(List.of(task));

        calendarController.updateCalendar();

        verify(taskStatusLabel, atLeastOnce()).setText("Check today's tasks, you have something going on!");
    }

    @Test
    void testUpdateCalendar_WithFutureTasks() {
        LocalDate futureDate = LocalDate.now().plusDays(3);
        Taskss futureTask = new Taskss(2L, "Future Task", "Description", LocalDateTime.of(futureDate, LocalDateTime.now().toLocalTime()), null, null, userId);

        when(taskService.getTasksByUserId(userId)).thenReturn(List.of(futureTask));

        calendarController.updateCalendar();

        verify(taskStatusLabel, atLeastOnce()).setText("You have tasks to do until " + futureDate);
    }

    @Test
    void testUpdateCalendar_WithMonthTasks() {
        YearMonth displayedMonth = YearMonth.now().plusMonths(1);
        LocalDate nextMonthDate = displayedMonth.atDay(5);
        Taskss nextMonthTask = new Taskss(3L, "Next Month Task", "Description", LocalDateTime.of(nextMonthDate, LocalDateTime.now().toLocalTime()), null, null, userId);

        when(taskService.getTasksByUserId(userId)).thenReturn(List.of(nextMonthTask));
        calendarController.goToNextMonth();
        calendarController.updateCalendar();

        verify(taskStatusLabel, atLeastOnce()).setText("First task in " + displayedMonth.getMonth() + ": " + nextMonthDate);
    }
}
