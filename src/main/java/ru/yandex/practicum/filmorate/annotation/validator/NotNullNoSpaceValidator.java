package ru.yandex.practicum.filmorate.annotation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.yandex.practicum.filmorate.annotation.NotNullNoSpace;

public class NotNullNoSpaceValidator implements ConstraintValidator<NotNullNoSpace, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value.isBlank()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Поле не может быть пустым").addConstraintViolation();
            return false;
        }
        if (value.contains(" ")) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Поле не может содержать пробелы").addConstraintViolation();
            return false;
        }
        return true;
    }
}