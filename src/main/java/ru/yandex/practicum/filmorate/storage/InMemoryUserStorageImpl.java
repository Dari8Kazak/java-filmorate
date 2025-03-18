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
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public User createUser(User user) {
        if (user == null) {
            throw new ValidationException("User", "user", "User must not be null");
        }
        if (user.getId() == null) {
            user.setId(getNextId());
        }
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User findById(Long id) {
        return users.values()
                .stream()
                .filter(user -> user.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("User not found userId: " + id));
    }

    public void save(User user) {
        users.put(user.getId(), user);
    }

    @Override
    public User updateUser(User user) {
        if (user == null) {
            throw new ValidationException("User", "user", "User is null");
        }
        if (!users.containsKey(user.getId())) {
            throw new NotFoundException("User not found userId: " + user.getId());
        }
        log.info("Updating user {}", user.getId());
        users.computeIfPresent(user.getId(), (id, oldUser) -> user);
        return users.get(user.getId());
    }

    @Override
    public void deleteUser(Long id) {
        if (id == null || !users.containsKey(id)) {
            throw new ValidationException("User", "id", "User is not found userId: " + id);
        }
        users.remove(id);
    }

    public void addNewFriend(Long userId, Long friendId) {
        friends.computeIfAbsent(userId, k -> new HashSet<>()).add(friendId);
        friends.computeIfAbsent(friendId, k -> new HashSet<>()).add(userId);
    }

    public boolean areFriends(Long userId, Long friendId) {
        return friends.getOrDefault(userId, Collections.emptySet()).contains(friendId);
    }

    public Set<Long> findFriends(Long userId) {
        return friends.getOrDefault(userId, Collections.emptySet());
    }

    public void removeFriends(Long userId, Long friendId) {
        friends.getOrDefault(userId, Collections.emptySet()).remove(friendId);
        friends.getOrDefault(friendId, Collections.emptySet()).remove(userId);
    }

    private Long getNextId() {
        long currentId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(1);
        return ++currentId;
    }
}