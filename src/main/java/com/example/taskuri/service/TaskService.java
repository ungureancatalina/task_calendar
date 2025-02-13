package com.example.taskuri.service;

import com.example.taskuri.domain.Taskss;
import com.example.taskuri.repository.Repository;
import com.example.taskuri.validation.ValidationException;
import com.example.taskuri.validation.Validator;

import java.time.LocalDate;
import java.util.List;

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

    public List<Taskss> getTasksByDate(LocalDate date) {
        return getTasksByDateRange(date, date);
    }

    public List<Taskss> getTasksByDateRange(LocalDate startDate, LocalDate endDate) {
        return taskRepository.getAll().stream()
                .filter(task -> !task.getStartDateTime().toLocalDate().isBefore(startDate) &&
                        (task.getFinishDateTime() == null || !task.getFinishDateTime().toLocalDate().isAfter(endDate)))
                .toList();
    }

    public void updateTask(Taskss task) throws ValidationException {
        taskValidator.validate(task);
        taskRepository.update(task);
    }

    public void deleteTask(Long id) {
        taskRepository.delete(id);
    }

    public List<Taskss> getTasksByStartDate(LocalDate date) {
        return taskRepository.getAll().stream()
                .filter(task -> task.getStartDateTime().toLocalDate().equals(date))
                .toList();
    }

}
