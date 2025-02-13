package com.example.taskuri.service;

import com.example.taskuri.domain.User;
import com.example.taskuri.repository.Repository;
import com.example.taskuri.validation.ValidationException;
import com.example.taskuri.validation.Validator;

import java.util.List;

public class UserService {
    private final Repository<User> userRepository;
    private final Validator<User> userValidator;

    public UserService(Repository<User> userRepository, Validator<User> userValidator) {
        this.userRepository = userRepository;
        this.userValidator = userValidator;
    }

    public void addUser(User user) throws ValidationException {
        userValidator.validate(user);
        userRepository.add(user);
    }

    public List<User> getAllUsers() {
        return userRepository.getAll();
    }

    public User getUserByEmail(String email) {
        return userRepository.getAll().stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst()
                .orElse(null);
    }

    public void deleteUser(Long id) {
        userRepository.delete(id);
    }
}
