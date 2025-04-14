package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    User createUser(User user);

    List<User> findAllUsers();

    User updateUser(User newUser);

    boolean containsUserById(Long id);

    User addFriend(Long userId, Long friendId);

    boolean removeFriend(Long userId, Long friendId);

    List<User> getFriendsList(Long userId);

    List<User> getCommonFriendsList(Long id, Long otherId);

    void removeUser(Long userId);

    User getUserById(Long id);

}