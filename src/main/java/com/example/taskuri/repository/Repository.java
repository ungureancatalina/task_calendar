package com.example.taskuri.repository;

import com.example.taskuri.domain.Taskss;
import com.example.taskuri.domain.User;

import java.util.List;

public interface Repository<T> {
    void add(T task);
    List<T> getAll();
    void delete(Long id);
    void update(T entity);

    List<T> getNotesByTaskId(Long taskId);

    User getUserByEmail(String email);

    List<Taskss> getTasksByUserId(Long userId);
}