package ru.yandex.practicum.filmorate.annotation.service;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserService {
    Collection<User> findAll();

    User createUser(User user);

    User updateUser(User user);

    void deleteUser(Long id);
}