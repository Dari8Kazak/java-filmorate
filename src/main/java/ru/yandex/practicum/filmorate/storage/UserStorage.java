package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    Collection<User> findAll();

    User createUser(User user);

    User updateUser(User user);

    void deleteUser(Long id);

    User findById(Long id);
}