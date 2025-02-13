package com.example.taskuri.validation;
import com.example.taskuri.domain.User;

class UserValidator implements Validator<User> {
    @Override
    public void validate(User user) throws ValidationException {
        if (user.getName() == null || user.getName().isEmpty()) {
            throw new ValidationException("Name cannot be empty");
        }
        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            throw new ValidationException("Invalid email format");
        }
    }
}