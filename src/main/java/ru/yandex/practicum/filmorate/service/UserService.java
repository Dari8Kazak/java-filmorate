package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserService {

    List<User> findAllUsers();

    List<User> getFriends(Long userId);

    List<User> getCommonFriends(Long userId, Long otherUserId);

    User createUser(User user);

    User updateUser(User user);

    User addFriend(Long userId, Long friendId);

    boolean removeFriend(Long userId, Long friendId);

    void removeUser(Long userId);

    User getUserById(Long userId);
}
