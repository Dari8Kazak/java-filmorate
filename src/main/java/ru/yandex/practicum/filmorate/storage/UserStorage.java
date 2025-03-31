package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Set;

public interface UserStorage {

    User createUser(User user);

    User updateUser(User user);

    void deleteUser(Long id);

    User getUserById(Long id);

    Collection<User> findAll();

    void addFriend(Long userId, Long friendId);

    Set<Long> getFriends(Long userId);

    void removeFriend(Long userId, Long friendId);
}