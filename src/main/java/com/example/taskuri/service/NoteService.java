package com.example.taskuri.service;

import com.example.taskuri.domain.Notes;
import com.example.taskuri.domain.Taskss;
import com.example.taskuri.domain.User;
import com.example.taskuri.repository.Repository;
import com.example.taskuri.validation.ValidationException;
import com.example.taskuri.validation.Validator;

import java.time.LocalDate;
import java.util.List;

public class NoteService {
    private final Repository<Notes> noteRepository;
    private final Validator<Notes> noteValidator;

    public NoteService(Repository<Notes> noteRepository, Validator<Notes> noteValidator) {
        this.noteRepository = noteRepository;
        this.noteValidator = noteValidator;
    }

    public void addNote(Notes note) throws ValidationException {
        noteValidator.validate(note);
        noteRepository.add(note);
    }

    public List<Notes> getAllNotes() {
        return noteRepository.getAll();
    }

    public void deleteNote(Long id) {
        noteRepository.delete(id);
    }

    public void modifyNote(Long id) {
        //noteRepository.update(id);
    }
}
