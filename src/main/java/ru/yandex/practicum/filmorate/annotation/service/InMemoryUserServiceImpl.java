package ru.yandex.practicum.filmorate.annotation.service;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class InMemoryUserServiceImpl implements UserService {
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public User createUser(User user) {
        if (user == null) {
            throw new ValidationException("User must not be null");
        }
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (user == null) {
            throw new ValidationException("User is null");
        }
        if (!users.containsKey(user.getId())) {
            throw new ValidationException("User not found");
        }
        log.info("Updating user {}", user.getId());
        // обновляем пользователя если его id уже есть
        users.computeIfPresent(user.getId(), (id, oldUser) -> user);
        return users.get(user.getId());
    }

    @Override
    public void deleteUser(Long id) {
        if (id == null || !users.containsKey(id)) {
            throw new ValidationException("User is not found");
        }
        users.remove(id);
    }

    private Long getNextId() {
        long currentId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentId;
    }
}