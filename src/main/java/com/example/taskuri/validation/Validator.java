package com.example.taskuri.validation;

public interface Validator<T> {
    void validate(T entity) throws ValidationException;
}