package ru.yandex.practicum.filmorate.service;

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

    public User createUser(User user) {
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public User getUserById(Long userId) throws NotFoundException {
        return userStorage.getUserById(userId);
    }

    public void deleteUser(Long userId) {
        userStorage.deleteUser(userId);
    }

    public Collection<User> findAllUsers() {
        return userStorage.findAll();
    }


    public List<User> getFriends(Long userId) {
        User user = userStorage.getUserById(userId);
        return user.getFriends().stream()
                .map(userStorage::getUserById)
                .toList();
    }

    public List<User> getCommonFriends(Long userId, Long otherUserId) {
        userStorage.getUserById(userId);
        userStorage.getUserById(otherUserId);

        Set<Long> userFriends = userStorage.getUserById(userId).getFriends();
        Set<Long> otherUserFriends = userStorage.getUserById(otherUserId).getFriends();

        return userFriends.stream()
                .filter(otherUserFriends::contains)
                .map(userStorage::getUserById)
                .toList();
    }

    public void addFriend(Long userId, Long friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        user.addFriend(friendId);
        friend.addFriend(userId);
    }

    public void removeFriend(Long userId, Long friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        user.removeFriend(friendId);
        friend.removeFriend(userId);
    }
}