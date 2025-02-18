package com.example.taskuri.tests;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.taskuri.domain.Notes;
import com.example.taskuri.repository.Repository;
import com.example.taskuri.service.NoteService;
import com.example.taskuri.validation.Validator;
import com.example.taskuri.validation.ValidationException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

class NoteServiceTest {

    private NoteService noteService;

    @Mock
    private Repository<Notes> noteRepository;

    @Mock
    private Validator<Notes> noteValidator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        noteService = new NoteService(noteRepository, noteValidator);
    }

    @Test
    void addNote_ShouldValidateAndSaveNote() throws ValidationException {
        Notes note = new Notes(1L, "Test Note");

        noteService.addNote(1L, "Test Note");

        verify(noteValidator).validate(any(Notes.class));
        verify(noteRepository).add(any(Notes.class));
    }

    @Test
    void getNotesForTask_ShouldReturnNotesList() {
        Notes note = new Notes(1L, "Test Note");
        when(noteRepository.getNotesByTaskId(1L)).thenReturn(List.of(note));

        List<Notes> notes = noteService.getNotesForTask(1L);

        assertFalse(notes.isEmpty());
        assertEquals("Test Note", notes.get(0).getContent());
    }

    @Test
    void deleteNote_ShouldCallRepositoryDelete() {
        noteService.deleteNote(1L);

        verify(noteRepository).delete(1L);
    }

    @Test
    void updateNote_ShouldCallRepositoryUpdate() {
        Notes note = new Notes(1L, "Updated Note");

        noteService.updateNote(note);

        verify(noteRepository).update(note);
    }
}
