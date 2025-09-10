package com.example.demo.config.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PasswordImpl implements ConstraintValidator<Password, String> {

    private final PasswordValidator passwordValidator;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return passwordValidator.validate(new PasswordData(value)).isValid();
    }
}