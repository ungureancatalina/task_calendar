package com.example.taskuri.controller;

import com.example.taskuri.domain.Notes;
import com.example.taskuri.domain.Taskss;
import com.example.taskuri.service.NoteService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.List;

public class NoteController {
    private NoteService noteService;
    private ObservableList<Notes> notesList;

    @FXML
    private ListView<Notes> notesListView;

    @FXML
    private TextArea noteContentTextArea;

    @FXML
    private Button addNoteButton, deleteNoteButton;

    private Taskss task;

    public void setNoteService(NoteService noteService) {
        this.noteService = noteService;
        loadNotes();
    }

    public void setTask(Taskss task) {
        this.task = task;
        if (noteService != null) {
            loadNotes();
        } else {
            System.err.println("Error: NoteService is null!");
        }
    }

    private void loadNotes() {
        List<Notes> notes = noteService.getAllNotes();
        notesList = FXCollections.observableArrayList(notes);
        notesListView.setItems(notesList);
    }

    @FXML
    public void initialize() {
        notesListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                noteContentTextArea.setText(newSelection.getContent());
                deleteNoteButton.setDisable(false);
            } else {
                noteContentTextArea.clear();
                deleteNoteButton.setDisable(true);
            }
        });

        deleteNoteButton.setDisable(true);
    }

    @FXML
    public void addNote() {
        String content = noteContentTextArea.getText().trim();
        if (content.isEmpty()) {
            showAlert("Error", "Note content cannot be empty!");
            return;
        }

        Notes newNote = new Notes(content);
        noteService.addNote(newNote);
        notesList.add(newNote);
        noteContentTextArea.clear();
    }

    @FXML
    public void deleteNote() {
        Notes selectedNote = notesListView.getSelectionModel().getSelectedItem();
        if (selectedNote == null) {
            showAlert("Error", "No note selected!");
            return;
        }

        noteService.deleteNote(selectedNote.getId());
        notesList.remove(selectedNote);
        noteContentTextArea.clear();
    }


    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void closeWindow() {
        Stage stage = (Stage) noteContentTextArea.getScene().getWindow();
        stage.close();
    }

}
