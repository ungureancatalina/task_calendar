package com.example.taskuri.tests;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.taskuri.domain.Notes;
import com.example.taskuri.repository.NoteRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;

class NoteRepositoryTest {

    private NoteRepositoryImpl noteRepository;

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
        noteRepository = new NoteRepositoryImpl(url, username, password);
    }

    @Test
    void testAddNote_Success() throws Exception {
        Notes note = new Notes(1L, "Test Content");

        when(mockConnection.prepareStatement(anyString(), anyInt())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getLong("id")).thenReturn(10L);

        noteRepository.add(note);

        assertNotNull(note.getId());
    }

    @Test
    void testGetNotesByTaskId_Success() throws Exception {
        Long taskId = 1L;

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getLong("id")).thenReturn(1L);
        when(mockResultSet.getString("content")).thenReturn("Test Note");
        when(mockResultSet.getTimestamp("created_at")).thenReturn(Timestamp.valueOf(LocalDateTime.now()));

        List<Notes> notes = noteRepository.getNotesByTaskId(taskId);

        assertFalse(notes.isEmpty());
        assertEquals(1, notes.size());
        assertEquals("Test Note", notes.get(0).getContent());
    }

    @Test
    void testUpdateNote_Success() throws Exception {
        Notes note = new Notes(1L, "Updated Content");

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        noteRepository.update(note);

        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    @Test
    void testDeleteNote_Success() throws Exception {
        Long noteId = 1L;

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        noteRepository.delete(noteId);

        verify(mockPreparedStatement, times(1)).executeUpdate();
    }
}
