package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Builder
@Data
public class User {
    private Long id;

    private String name;

    @Pattern(regexp = "^(?!.*\\s).+$", message = "Логин не должен содержать пробелов")
    private String login;

    @Email(message = "Некорректный адрес электронной почты")
    @Pattern(regexp = "^(?!.*\\s).+$", message = "Email не должен содержать пробелов")
    private String email;

    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    @NotNull(message = "Дата рождения не может быть пустой")
    private LocalDate birthday;
}