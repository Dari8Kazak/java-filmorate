package ru.yandex.practicum.filmorate.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ru.yandex.practicum.filmorate.annotation.validator.NotNullNoSpaceValidator;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = NotNullNoSpaceValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface NotNullNoSpace {
    String message() default "{Поле не может быть null или содержать пробелы}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}