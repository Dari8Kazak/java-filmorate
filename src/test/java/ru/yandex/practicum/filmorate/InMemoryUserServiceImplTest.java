package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorageImpl;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryUserServiceImplTest {
    private UserStorage userService;
    private User user;


    @BeforeEach
    void setUp() {
        userService = new InMemoryUserStorageImpl();
        user = User.builder()
                .id(1L)
                .name("Jane")
                .login("JaneLogin")
                .email("Jane@email.com")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
    }

    @DisplayName("Успешное создание пользователя")
    @Test
    void testCreateUser() {
        userService.createUser(user);
        Collection<User> allUsers = userService.findAll();
        assertTrue(allUsers.contains(user));
        assertEquals(1, allUsers.size());
    }

    @DisplayName("Успешное обновление пользователя")
    @Test
    void testUpdateUser() {
        userService.createUser(user);
        User user1 = User.builder()
                .id(1L)
                .name("Den")
                .login("login")
                .email("den@email.com")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        User updated = userService.updateUser(user1);
        assertEquals("Den", updated.getName());
        assertEquals("login", updated.getLogin());
    }

    @DisplayName("Ошибка обновление фильма")
    @Test
    void testUpdateFilmFailed() {
        Exception exception = assertThrows(NotFoundException.class, () -> userService.updateUser(user));
        assertEquals("User not found userId: " + user.getId(), exception.getMessage());
    }

    @DisplayName("Успешное удаление пользователя")
    @Test
    void testDeleteUser() {
        userService.createUser(user);
        Collection<User> users = userService.findAll();
        assertTrue(users.contains(user));
        userService.deleteUser(user.getId());
        users = userService.findAll();
        assertTrue(users.isEmpty());
    }
}
