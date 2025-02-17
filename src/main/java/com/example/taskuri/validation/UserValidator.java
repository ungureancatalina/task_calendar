package com.example.taskuri.validation;

import com.example.taskuri.domain.User;
import java.util.regex.Pattern;

public class UserValidator implements Validator<User> {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{6,}$");

    @Override
    public void validate(User user) throws ValidationException {
        if (user.getEmail() == null || !EMAIL_PATTERN.matcher(user.getEmail()).matches()) {
            throw new ValidationException("Invalid email format. It must contain '@' and end with a valid domain (e.g., .com, .net).");
        }

        if (user.getPassword() == null || !PASSWORD_PATTERN.matcher(user.getPassword()).matches()) {
            throw new ValidationException("Password must be at least 6 characters long and contain at least one digit, one lowercase letter, and one uppercase letter.");
        }
    }
}
