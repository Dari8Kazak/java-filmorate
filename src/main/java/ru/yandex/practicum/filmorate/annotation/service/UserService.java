package ru.yandex.practicum.filmorate.annotation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.FriendDto;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorageImpl;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final InMemoryUserStorageImpl userStorage;

    public User getUserById(Long userId) {
        return userStorage.findById(userId);
    }

    public List<FriendDto> findFriends(Long userId) {
        return userStorage.findFriends(userId).stream()
                .map(FriendDto::new)
                .collect(Collectors.toList());
    }

    public List<FriendDto> findCommonFriends(Long userId, Long friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);
        List<FriendDto> userFriends = findFriends(user.getId());
        List<FriendDto> friendsFriends = findFriends(friend.getId());
        return userFriends.stream()
                .filter(friendsFriends::contains)
                .collect(Collectors.toList());
    }

    public List<FriendDto> findAllFriend(Long userId) {
        User user = userStorage.findById(userId);
        return userStorage.findFriends(user.getId())
                .stream()
                .map(FriendDto::new)
                .collect(Collectors.toList());
    }

    public boolean addFriends(Long userId, Long friendId) {
        if (userId.equals(friendId)) {
            throw new ValidationException("User", "userId", "Нельзя добавить самого себя в друзья");
        }
        if (userStorage.areFriends(userId, friendId)) {
            throw new ValidationException("User", "userId", "Друг уже есть в друзья с id: " + userId);
        }
        userStorage.addNewFriend(userId, friendId);
        return true;
    }

    public boolean deleteFriendById(Long userId, Long friendId) {
        if (userStorage.areFriends(userId, friendId)) {
            userStorage.removeFriends(userId, friendId);
        }
        return true;
    }
}