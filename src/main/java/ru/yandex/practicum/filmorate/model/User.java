package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.annotation.NotNullNoSpace;

import java.time.LocalDate;

@Builder
@Data
public class User {
    private Long id;

    private String name;

    @NotNullNoSpace
    private String login;

    @Email
    @NotNullNoSpace
    private String email;

    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    @NotNull(message = "Дата рождения не может быть пустой")
    private LocalDate birthday;
}