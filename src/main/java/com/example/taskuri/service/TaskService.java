package com.example.taskuri.service;

import com.example.taskuri.domain.Taskss;
import com.example.taskuri.repository.Repository;
import com.example.taskuri.validation.ValidationException;
import com.example.taskuri.validation.Validator;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TaskService {
    private final Repository<Taskss> taskRepository;
    private final Validator<Taskss> taskValidator;

    public TaskService(Repository<Taskss> taskRepository, Validator<Taskss> taskValidator) {
        this.taskRepository = taskRepository;
        this.taskValidator = taskValidator;
    }

    public void addTask(Taskss task) throws ValidationException {
        taskValidator.validate(task);
        taskRepository.add(task);
    }

    public List<Taskss> getAllTasks() {
        return taskRepository.getAll();
    }

    public List<Taskss> getTasksByUserId(Long userId) {
        return taskRepository.getAll().stream()
                .filter(task -> task.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    public List<Taskss> getTasksByUserAndDate(Long userId, LocalDate date) {
        return taskRepository.getTasksByUserId(userId).stream()
                .filter(task ->
                        !task.getStartDateTime().toLocalDate().isAfter(date) &&
                                (task.getFinishDateTime() == null || !task.getFinishDateTime().toLocalDate().isBefore(date))
                )
                .toList();
    }


    public void updateTask(Taskss task) throws ValidationException {
        taskValidator.validate(task);
        taskRepository.update(task);
    }

    public void deleteTask(Long id) {
        taskRepository.delete(id);
    }
}
