package ru.yandex.practicum.filmorate.annotation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {
    @Autowired
    private UserStorage userStorage;
    private final Map<Long, Set<Long>> friends;

    public User createUser(User user) {
        Objects.requireNonNull(user, "Пользователь не должен быть null");
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        Objects.requireNonNull(user, "Пользователь не должен быть null");
        return userStorage.updateUser(user);
    }

    public User getUserById(Long userId) throws NotFoundException {
        Objects.requireNonNull(userId, "Идентификатор пользователя не может быть null");
        return userStorage.getUserById(userId);
    }

    public void deleteUser(Long userId) {
        Objects.requireNonNull(userId, "Идентификатор пользователя не может быть null");
        userStorage.deleteUser(userId);
    }

    public Collection<User> findAllUsers() {
        return userStorage.findAll();
    }

    public void addFriend(Long userId, Long friendId) {
        userStorage.getUserById(userId);
        userStorage.getUserById(friendId);
        userStorage.addFriend(userId, friendId);
    }

    public void removeFriend(Long userId, Long friendId) {
        userStorage.getUserById(userId);
        userStorage.getUserById(friendId);
        userStorage.removeFriend(userId, friendId);
    }

    public List<User> getFriends(Long userId) {
        userStorage.getUserById(userId);
        return userStorage.getFriends(userId).stream()
                .map(userStorage::getUserById)
                .toList();
    }

    public List<User> getCommonFriends(Long userId, Long otherUserId) {
        userStorage.getUserById(userId);
        userStorage.getUserById(otherUserId);

        Set<Long> userFriends = userStorage.getFriends(userId);
        Set<Long> otherUserFriends = userStorage.getFriends(otherUserId);

        return userFriends.stream()
                .filter(otherUserFriends::contains)
                .map(userStorage::getUserById)
                .toList();
    }
}