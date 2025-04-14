package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import ru.yandex.practicum.filmorate.annotation.ValidReleaseDate;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@Data
public class Film {
    private Long id;

    @NotBlank(message = "Название не может быть пустым")
    private String name;

    @Size(max = 200, message = "Максимальная длина описания не должна превышать 200 символов")
    @NotBlank(message = "Описание не должно быть пустым")
    private String description;

    @NotNull(message = "Дата релиза не должна быть пустой")
    @ValidReleaseDate
    private LocalDate releaseDate;

    @PositiveOrZero
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private Long duration;

    private Set<Long> likes = new HashSet<>();

    @Setter
    @Getter
    private Set<Genre> genres;

    @Setter
    @Getter
    private RatingMPA mpa;
}