package ru.yandex.practicum.filmorate.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ru.yandex.practicum.filmorate.annotation.validator.ValidReleaseDateValidator;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Constraint(validatedBy = ValidReleaseDateValidator.class)
public @interface ValidReleaseDate {
    String message() default "{Дата релиза — не должна быть раньше 28 декабря 1895 года}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}