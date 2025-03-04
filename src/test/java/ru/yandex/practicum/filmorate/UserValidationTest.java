package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserValidationTest {
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @DisplayName("Успешная валидация пользователя")
    @Test
    void testValidUser() {
        User user = User.builder()
                .id(1L)
                .birthday(LocalDate.of(1999, 12, 31))
                .email("email@email.com")
                .name("name")
                .login("login")
                .build();
        Set<ConstraintViolation<User>> constraintViolations = validator.validate(user);
        assertTrue(constraintViolations.isEmpty());
    }

    @DisplayName("Успешная валидация имя пользователя может быть пустым")
    @Test
    void testFailedValidName() {
        User user = User.builder()
                .id(1L)
                .birthday(LocalDate.of(1999, 12, 31))
                .email("email@email.com")
                .name("")
                .login("login")
                .build();
        Set<ConstraintViolation<User>> constraintViolations = validator.validate(user);
        assertTrue(constraintViolations.isEmpty());
    }

    @DisplayName("Ошибка валидация логин пользователя не должно быть пустым")
    @Test
    void testFailedValidLoginIsBlank() {
        User user = User.builder()
                .id(1L)
                .birthday(LocalDate.of(1999, 12, 31))
                .email("email@email.com")
                .name("name")
                .login("")
                .build();
        Set<ConstraintViolation<User>> constraintViolations = validator.validate(user);
        assertFalse(constraintViolations.isEmpty());
    }

    @DisplayName("Ошибка валидация логин пользователя не должно быть пустым")
    @Test
    void testFailedValidLoginWithSpace() {
        User user = User.builder()
                .id(1L)
                .birthday(LocalDate.of(1999, 12, 31))
                .email("email@email.com")
                .name("name")
                .login("login login")
                .build();
        Set<ConstraintViolation<User>> constraintViolations = validator.validate(user);
        assertFalse(constraintViolations.isEmpty());
    }

    @DisplayName("Ошибка валидация пользователя не корректный email")
    @Test
    void testFailedValidEmail() {
        User user = User.builder()
                .id(1L)
                .birthday(LocalDate.of(1999, 12, 31))
                .email("emailsdemail.com")
                .name("name")
                .login("login")
                .build();
        Set<ConstraintViolation<User>> constraintViolations = validator.validate(user);
        assertFalse(constraintViolations.isEmpty());
    }

    @DisplayName("Успешная валидация email")
    @Test
    void testValidEmail() {
        User user = User.builder()
                .id(1L)
                .birthday(LocalDate.of(1999, 12, 31))
                .email("email@mail.com")
                .name("name")
                .login("login")
                .build();
        Set<ConstraintViolation<User>> constraintViolations = validator.validate(user);
        assertTrue(constraintViolations.isEmpty());
    }

    @DisplayName("Ошибка валидация дата рождения не должно быть в будущем")
    @Test
    void testFailedValidBirthday() {
        User user = User.builder()
                .id(1L)
                .birthday(LocalDate.of(2025, 12, 31))
                .email("email@mail.com")
                .name("name")
                .login("login")
                .build();
        Set<ConstraintViolation<User>> constraintViolations = validator.validate(user);
        assertFalse(constraintViolations.isEmpty());
    }
}