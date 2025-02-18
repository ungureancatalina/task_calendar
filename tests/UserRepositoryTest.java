package com.example.taskuri.tests;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.taskuri.domain.User;
import com.example.taskuri.repository.UserRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.*;

class UserRepositoryTest {

    private UserRepositoryImpl userRepository;

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
        userRepository = new UserRepositoryImpl(url, username, password);
    }

    @Test
    void testAddUser_Success() throws Exception {
        User user = new User(null, "test@example.com", "password123");

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        userRepository.add(user);

        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    @Test
    void testGetUserByEmail_Success() throws Exception {
        String testEmail = "test@example.com";

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getLong("id")).thenReturn(1L);
        when(mockResultSet.getString("email")).thenReturn(testEmail);
        when(mockResultSet.getString("password")).thenReturn("password123");

        User user = userRepository.getUserByEmail(testEmail);

        assertNotNull(user);
        assertEquals(1L, user.getId());
        assertEquals(testEmail, user.getEmail());
        assertEquals("password123", user.getPassword());
    }

    @Test
    void testGetUserByEmail_NotFound() throws Exception {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        User user = userRepository.getUserByEmail("nonexistent@example.com");

        assertNull(user);
    }

    @Test
    void testDeleteUser_NotImplemented() {
        assertDoesNotThrow(() -> userRepository.delete(1L));
    }

    @Test
    void testUpdateUser_NotImplemented() {
        User user = new User(1L, "update@example.com", "newpass");
        assertDoesNotThrow(() -> userRepository.update(user));
    }
}
