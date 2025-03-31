package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorageImpl implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private final Map<Long, Set<Long>> friends = new HashMap<>();

    @Override
    public User createUser(User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            throw new NotFoundException("User not found userId: " + user.getId());
        }
        log.info("обновление юзера {}", user.getId());
        users.computeIfPresent(user.getId(), (id, oldUser) -> user);
        return users.get(user.getId());
    }

    @Override
    public User getUserById(Long userId) {
        if (!users.containsKey(userId)) {
            throw new NotFoundException("User not found userId: " + userId);
        }
        return users.get(userId);
    }

    @Override
    public void deleteUser(Long userId) {
        if (!users.containsKey(userId)) {
            throw new ValidationException("User", "id", "User is not found userId: " + userId);
        }
        users.remove(userId);
    }

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    private Long getNextId() {
        long currentId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentId;
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        friends.computeIfAbsent(userId, k -> new HashSet<>()).add(friendId);
        friends.computeIfAbsent(friendId, k -> new HashSet<>()).add(userId);
    }

    @Override
    public Set<Long> getFriends(Long userId) {
        return friends.getOrDefault(userId, Collections.emptySet());
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        friends.getOrDefault(userId, Collections.emptySet()).remove(friendId);
        friends.getOrDefault(friendId, Collections.emptySet()).remove(userId);
    }
}