package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FilmValidatorTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @DisplayName("Успешная валидация объекта")
    @Test
    void testPassValidFilm() {
        Film film = Film.builder()
                .id(1L)
                .name("Test Film")
                .description("Test Description")
                .releaseDate(LocalDate.now())
                .duration(30L)
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty());
    }

    @DisplayName("Ошибка валидация названия")
    @Test
    void testFailedValidFilmName() {
        Film film = Film.builder()
                .id(2L)
                .name("")
                .description("Test Description")
                .releaseDate(LocalDate.now())
                .duration(30L)
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Ошибка: имя не может быть пустым");
    }

    @DisplayName("Ошибка валидация описания")
    @Test
    void testFailedValidFilmDescription() {
        Film film = Film.builder()
                .id(2L)
                .name("Test Film")
                .description("")
                .releaseDate(LocalDate.now())
                .duration(30L)
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Ошибка: описание не может быть пустым");
    }

    @DisplayName("Ошибка валидация duration не должен быть отрицательным")
    @Test
    void testFailedValidFilmDuration() {
        Film film = Film.builder()
                .id(1L)
                .name("Test Film")
                .description("Test Description")
                .releaseDate(LocalDate.now())
                .duration(-30L)
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Ошибка: duration не может быть пустым");
    }

    @DisplayName("Ошибка валидация даты релиза не должна быть раньше 28.12.1895")
    @Test
    void testFailedValidFilmReleaseDate() {
        Film film = Film.builder()
                .id(1L)
                .name("Test Film")
                .description("Test Description")
                .releaseDate(LocalDate.parse("1895-12-27"))
                .duration(30L)
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Ошибка: дата не должна быть раньше 28.12.1895");
    }

}