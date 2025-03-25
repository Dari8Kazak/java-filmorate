package ru.yandex.practicum.filmorate.annotation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.FriendDto;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    private final Map<Long, Set<Long>> friends;

    public User createUser(User user) {
        if (user == null) {
            throw new ValidationException("User", "user", "User must not be null");
        }
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        if (user == null) {
            throw new ValidationException("User", "user", "User is null");
        }
        return userStorage.updateUser(user);
    }

    public User getUserById(Long userId) {
        if (userId == null) {
            throw new NotFoundException("User not found userId: " + userId);
        }
        return userStorage.getUserById(userId);
    }

    public void deleteUser(Long userId) {
        if (userId == null) {
            throw new ValidationException("User", "id", "User is null");
        }
        userStorage.deleteUser(userId);
    }

    public Collection<User> findAllUsers() {
        return userStorage.findAll();
    }

    //работа с друзьями

    public boolean addFriends(Long userId, Long friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);

        if (user.getId().equals(friend.getId())) {
            throw new ValidationException("User", "userId", "Нельзя добавить самого себя в друзья");
        }
        if (findFriends(userId).contains(friendId)) {
            throw new ValidationException("User", "userId", "Друг уже есть в друзья с id: " + friendId);
        }
        user.addFriend(friendId);
        friend.addFriend(userId);
        return true;
    }

    public boolean deleteFriendById(Long userId, Long friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);

        if (!findFriends(userId).contains(friendId)) {
            throw new ValidationException("User", "friendId", "Друг с id " + friendId + " не найден в списке друзей");
        }
        user.removeFriend(friendId);
        friend.removeFriend(userId);
        return true;
    }

    public List<FriendDto> findAllFriend(Long userId) {
        return findFriends(userId)
                .stream()
                .map(FriendDto::new)
                .collect(Collectors.toList());
    }

    public List<Long> findCommonFriends(Long userId, Long friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        Set<Long> userFriends = findFriends(user.getId());
        Set<Long> friendsFriends = findFriends(friend.getId());
        return userFriends.stream()
                .filter(friendsFriends::contains)
                .collect(Collectors.toList());
    }

    public Set<Long> findFriends(Long userId) {
        return friends.getOrDefault(userId, Collections.emptySet());
    }
}
