package com.example.taskuri.service;

import com.example.taskuri.domain.User;
import com.example.taskuri.repository.Repository;
import com.example.taskuri.validation.ValidationException;
import com.example.taskuri.validation.Validator;
import java.util.Optional;

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

    public User getUserByEmail(String email) {
        return userRepository.getUserByEmail(email);
    }

    public boolean checkUserCredentials(String email, String password) {
        User user = getUserByEmail(email);
        return user != null && user.getPassword().equals(password);
    }

    public Optional<User> authenticateUser(String email, String password) {
        User user = getUserByEmail(email);
        if (user != null && user.getPassword().equals(password)) {
            return Optional.of(user);
        }
        return Optional.empty();
    }
}
