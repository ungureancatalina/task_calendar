package com.example.taskuri.controller;

import com.example.taskuri.domain.Notes;
import com.example.taskuri.domain.Taskss;
import com.example.taskuri.service.NoteService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class NoteController {
    private NoteService noteService;
    private Taskss task;
    private ObservableList<Notes> notesList;

    @FXML
    private TextArea taskDescriptionTextArea;
    @FXML
    private ListView<Notes> notesListView;
    @FXML
    private TextArea noteContentTextArea;
    @FXML
    private Button addNoteButton, modifyNoteButton, deleteNoteButton;
    @FXML
    private TextArea notesTextArea;


    public void setNoteService(NoteService noteService) {
        this.noteService = noteService;
    }

    public void setTask(Taskss task) {
        this.task = task;
        taskDescriptionTextArea.setText(task.getDescription());
        loadNotes();
    }

    private void loadNotes() {
        if (task == null || noteService == null) {
            return;
        }

        List<Notes> notes = noteService.getNotesForTask(task.getId());
        notesList = FXCollections.observableArrayList(notes);
        notesListView.setItems(notesList);
    }

    @FXML
    public void initialize() {
        notesListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                noteContentTextArea.setText(newSelection.getContent());
                modifyNoteButton.setDisable(false);
                deleteNoteButton.setDisable(false);
            } else {
                noteContentTextArea.clear();
                modifyNoteButton.setDisable(true);
                deleteNoteButton.setDisable(true);
            }
        });

        notesListView.setStyle("-fx-background-color: #bda8e0;");
        notesListView.setCellFactory(lv -> new ListCell<Notes>() {
            @Override
            protected void updateItem(Notes item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setStyle("-fx-background-color: #bda8e0; -fx-font-weight: bold; -fx-text-fill: black;");
                } else {
                    setText(item.toString());
                    if (isSelected()) {
                        setStyle("-fx-background-color: #d8c8f5; -fx-font-weight: bold; -fx-text-fill: black;");
                    } else {
                        setStyle("-fx-background-color: #bda8e0; -fx-font-weight: bold; -fx-text-fill: black;");
                    }
                }
            }
        });

        notesListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            notesListView.refresh();
        });

        notesTextArea.setText(
                "- To **add** a note, type in the text box and click \"Add Note\".\n\n" +
                        "- To **modify** a note, select it from the list, edit the text, then click \"Modify Note\".\n\n" +
                        "- To **delete** a note, select it from the list and click \"Delete Note\"."
        );
    }

    @FXML
    public void addNote() {
        String content = noteContentTextArea.getText().trim();
        if (content.isEmpty()) {
            showAlert("Error", "Note content cannot be empty!");
            return;
        }

        Notes newNote = new Notes(task.getId(), content);
        noteService.addNote(task.getId(), content);
        notesList.add(newNote);
        noteContentTextArea.clear();
    }

    @FXML
    public void modifyNote() {
        Notes selectedNote = notesListView.getSelectionModel().getSelectedItem();
        if (selectedNote == null) {
            showAlert("Error", "No note selected!");
            return;
        }

        if (selectedNote.getId() == null) {
            showAlert("Error", "This note cannot be modified as it has no valid ID.");
            return;
        }

        String updatedContent = noteContentTextArea.getText().trim();
        if (updatedContent.isEmpty()) {
            showAlert("Error", "Modified note content cannot be empty!");
            return;
        }

        selectedNote.setContent(updatedContent);
        noteService.updateNote(selectedNote);
        notesListView.refresh();
        noteContentTextArea.clear();
    }
    @FXML
    public void deleteNote() {
        Notes selectedNote = notesListView.getSelectionModel().getSelectedItem();
        if (selectedNote == null) {
            showAlert("Error", "No note selected!");
            return;
        }

        if (selectedNote.getId() == null) {
            showAlert("Error", "This note cannot be deleted as it has no valid ID.");
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
}
