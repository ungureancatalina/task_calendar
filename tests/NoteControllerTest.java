package com.example.taskuri.tests;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.taskuri.controller.NoteController;
import com.example.taskuri.domain.Notes;
import com.example.taskuri.domain.Taskss;
import com.example.taskuri.service.NoteService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

class NoteControllerTest {

    private NoteController noteController;

    @Mock
    private NoteService noteService;

    @Mock
    private Taskss task;

    @Mock
    private ListView<Notes> notesListView;

    @Mock
    private TextArea taskDescriptionTextArea, noteContentTextArea, notesTextArea;

    @Mock
    private Button addNoteButton, modifyNoteButton, deleteNoteButton;

    private ObservableList<Notes> notesList;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        noteController = new NoteController();
        noteController.setNoteService(noteService);
        notesList = FXCollections.observableArrayList();
    }

    @Test
    void setTask_ShouldLoadNotesAndSetDescription() {
        when(task.getDescription()).thenReturn("Sample Task Description");
        when(task.getId()).thenReturn(1L);
        when(noteService.getNotesForTask(1L)).thenReturn(List.of(new Notes(1L, "Test Note")));

        noteController.setTask(task);

        verify(taskDescriptionTextArea).setText(contains("Sample Task Description"));
    }

    @Test
    void addNote_Valid_ShouldAddToServiceAndList() {
        when(noteContentTextArea.getText()).thenReturn("New Note");
        when(task.getId()).thenReturn(1L);

        noteController.addNote();

        verify(noteService).addNote(1L, "New Note");
    }

    @Test
    void addNote_EmptyContent_ShouldShowError() {
        when(noteContentTextArea.getText()).thenReturn("");

        noteController.addNote();
        verify(noteService, never()).addNote(anyLong(), anyString());
    }

    @Test
    void modifyNote_Valid_ShouldUpdateService() {
        Notes note = new Notes(1L, "Old Note");
        when(notesListView.getSelectionModel().getSelectedItem()).thenReturn(note);
        when(noteContentTextArea.getText()).thenReturn("Updated Note");

        noteController.modifyNote();

        verify(noteService).updateNote(any(Notes.class));
    }

    @Test
    void deleteNote_Valid_ShouldRemoveFromService() {
        Notes note = new Notes(1L, "Test Note");
        when(notesListView.getSelectionModel().getSelectedItem()).thenReturn(note);

        noteController.deleteNote();

        verify(noteService).deleteNote(1L);
    }

    @Test
    void deleteNote_NoSelection_ShouldShowError() {
        when(notesListView.getSelectionModel().getSelectedItem()).thenReturn(null);

        noteController.deleteNote();
        verify(noteService, never()).deleteNote(anyLong());
    }
}
