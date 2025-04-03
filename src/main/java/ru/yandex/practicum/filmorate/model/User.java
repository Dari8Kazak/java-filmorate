package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

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

    @Builder.Default
    private Set<Long> friends = new HashSet<>();

    public void addFriend(Long id) {
        if (friends == null) {
            this.friends = new HashSet<>();
        }
        friends.add(id);
    }

    public void removeFriend(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Идентификатор друга не может быть null");
        }
        friends.remove(id);
    }

    public Set<Long> getFriends() {
        if (friends == null) {
            this.friends = new HashSet<>();
        }
        return friends;
    }
}
