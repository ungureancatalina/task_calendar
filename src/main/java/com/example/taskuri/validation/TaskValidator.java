package com.example.taskuri.validation;

import com.example.taskuri.domain.Taskss;

public class TaskValidator implements Validator<Taskss> {
    @Override
    public void validate(Taskss task) throws ValidationException {
        if (task.getTitle() == null || task.getTitle().isEmpty()) {
            throw new ValidationException("Title cannot be empty");
        }

        if (task.getStartDateTime() == null) {
            throw new ValidationException("Start date and time must be provided");
        }

        if (task.getFinishDateTime() != null && task.getFinishDateTime().isBefore(task.getStartDateTime())) {
            throw new ValidationException("Finish date and time cannot be earlier than start date and time");
        }
    }
}
