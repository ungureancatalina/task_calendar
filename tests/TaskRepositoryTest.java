package com.example.taskuri.tests;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.taskuri.domain.TaskStatus;
import com.example.taskuri.domain.Taskss;
import com.example.taskuri.repository.TaskRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;

class TaskRepositoryTest {

    private TaskRepositoryImpl taskRepository;

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockPreparedStatement;

    @Mock
    private ResultSet mockResultSet;

    private final String url = "jdbc:postgresql://localhost:5432/testdb";
    private final String username = "postgres";
    private final String password = "catalina";

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        taskRepository = new TaskRepositoryImpl(url, username, password);
    }

    @Test
    void testAddTask_Success() throws Exception {
        Taskss task = new Taskss(null, "Test Task", "Description",
                LocalDateTime.now(), null, TaskStatus.TO_DO, 1L);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        taskRepository.add(task);

        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    @Test
    void testGetAllTasks_Success() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getLong("id")).thenReturn(1L);
        when(mockResultSet.getString("title")).thenReturn("Test Task");
        when(mockResultSet.getString("description")).thenReturn("Test Description");
        when(mockResultSet.getTimestamp("start_date")).thenReturn(Timestamp.valueOf(LocalDateTime.now()));
        when(mockResultSet.getString("status")).thenReturn("TO_DO");
        when(mockResultSet.getLong("user_id")).thenReturn(1L);

        List<Taskss> tasks = taskRepository.getAll();

        assertFalse(tasks.isEmpty());
        assertEquals(1, tasks.size());
        assertEquals("Test Task", tasks.get(0).getTitle());
    }

    @Test
    void testUpdateTask_Success() throws Exception {
        Taskss task = new Taskss(1L, "Updated Task", "Updated Description",
                LocalDateTime.now(), null, TaskStatus.IN_PROGRESS, 1L);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        taskRepository.update(task);

        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    @Test
    void testDeleteTask_Success() throws Exception {
        Long taskId = 1L;

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        taskRepository.delete(taskId);

        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    @Test
    void testGetTasksByUserId_Success() throws Exception {
        Long userId = 1L;

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getLong("id")).thenReturn(1L);
        when(mockResultSet.getString("title")).thenReturn("User Task");
        when(mockResultSet.getString("description")).thenReturn("User Task Description");
        when(mockResultSet.getTimestamp("start_date")).thenReturn(Timestamp.valueOf(LocalDateTime.now()));
        when(mockResultSet.getString("status")).thenReturn("TO_DO");
        when(mockResultSet.getLong("user_id")).thenReturn(1L);

        List<Taskss> tasks = taskRepository.getTasksByUserId(userId);

        assertFalse(tasks.isEmpty());
        assertEquals(1, tasks.size());
        assertEquals("User Task", tasks.get(0).getTitle());
    }
}
