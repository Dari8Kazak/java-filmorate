package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Service
@RequiredArgsConstructor
public class InMemoryUserService implements UserService {
    private final UserStorage userStorage;

    public List<User> findAllUsers() {
        return userStorage.findAllUsers();
    }

    @SneakyThrows
    public List<User> getFriends(Long userId) {
        return userStorage.getFriendsList(userId);
    }

    @SneakyThrows
    public List<User> getCommonFriends(Long userId, Long otherUserId) {
        return userStorage.getCommonFriendsList(userId, otherUserId);
    }

    public User createUser(User user) {
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    @SneakyThrows
    public User addFriend(Long userId, Long friendId) {
        return userStorage.addFriend(userId, friendId);
    }

    @SneakyThrows
    public boolean removeFriend(Long userId, Long friendId) {
        return userStorage.removeFriend(userId, friendId);

    }

    public void removeUser(Long userId) {
        userStorage.removeUser(userId);
    }

    public User getUserById(Long userId) throws NotFoundException {
        return userStorage.getUserById(userId);
    }
}