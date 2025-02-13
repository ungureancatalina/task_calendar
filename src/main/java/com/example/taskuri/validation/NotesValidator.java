package com.example.taskuri.validation;

import com.example.taskuri.domain.Notes;

public class NotesValidator implements Validator<Notes> {
    @Override
    public void validate(Notes note) throws ValidationException {
        if (note.getContent() == null || note.getContent().isEmpty()) {
            throw new ValidationException("Note content cannot be empty");
        }
    }
}
