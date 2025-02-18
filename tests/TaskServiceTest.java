package com.example.taskuri.tests;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.taskuri.domain.Taskss;
import com.example.taskuri.repository.Repository;
import com.example.taskuri.validation.ValidationException;
import com.example.taskuri.validation.Validator;
import com.example.taskuri.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;

class TaskServiceTest {

    private TaskService taskService;

    @Mock
    private Repository<Taskss> taskRepository;

    @Mock
    private Validator<Taskss> taskValidator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        taskService = new TaskService(taskRepository, taskValidator);
    }

    @Test
    void addTask_ValidTask_ShouldPass() throws ValidationException {
        Taskss task = new Taskss(1L, "Title", "Description", LocalDateTime.now(), null, null, 1L);
        taskService.addTask(task);
        verify(taskValidator).validate(task);
        verify(taskRepository).add(task);
    }

    @Test
    void addTask_InvalidTask_ShouldThrowException() throws ValidationException {
        Taskss task = new Taskss(null, "", "", null, null, null, 1L);
        doThrow(new ValidationException("Invalid task")).when(taskValidator).validate(task);

        assertThrows(ValidationException.class, () -> taskService.addTask(task));
    }

    @Test
    void getTasksByUserId_ShouldReturnList() {
        when(taskRepository.getAll()).thenReturn(List.of(new Taskss(1L, "Title", "Desc", LocalDateTime.now(), null, null, 1L)));

        List<Taskss> tasks = taskService.getTasksByUserId(1L);
        assertFalse(tasks.isEmpty());
        assertEquals(1, tasks.size());
    }

    @Test
    void deleteTask_ShouldCallRepositoryDelete() {
        taskService.deleteTask(1L);
        verify(taskRepository).delete(1L);
    }
}
