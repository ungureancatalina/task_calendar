package com.example.taskuri.repository;

import java.util.List;
public interface Repository<T> {
    void add(T task);
    List<T> getAll();
    void delete(Long id);
    void update(T entity);
}