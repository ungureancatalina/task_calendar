package com.example.taskuri.service;

import com.example.taskuri.domain.Notes;
import com.example.taskuri.repository.Repository;
import com.example.taskuri.validation.ValidationException;
import com.example.taskuri.validation.Validator;

import java.util.List;

public class NoteService {
    private final Repository<Notes> noteRepository;
    private final Validator<Notes> noteValidator;

    public NoteService(Repository<Notes> noteRepository, Validator<Notes> noteValidator) {
        this.noteRepository = noteRepository;
        this.noteValidator = noteValidator;
    }

    public void addNote(Long taskId, String content) throws ValidationException {
        Notes newNote = new Notes(taskId, content);
        noteValidator.validate(newNote);
        noteRepository.add(newNote);
    }

    public List<Notes> getNotesForTask(Long taskId) {
        return noteRepository.getNotesByTaskId(taskId);
    }

    public void deleteNote(Long id) {
        noteRepository.delete(id);
    }

    public void updateNote(Notes note) {
        noteRepository.update(note);
    }

}
