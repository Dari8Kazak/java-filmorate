package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.service.InMemoryUserService;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final InMemoryUserService userService;

    @GetMapping
    public Collection<User> findAllUsers() {
        return userService.findAllUsers();
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable Long id) {
        Objects.requireNonNull(id, "Идентификатор пользователя не может быть null");
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        Objects.requireNonNull(id, "Идентификатор пользователя не может быть null");
        Objects.requireNonNull(otherId, "Идентификатор пользователя не может быть null");
        return userService.getCommonFriends(id, otherId);
    }

    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        Objects.requireNonNull(user, "Пользователь не может быть null");
        User createdUser = userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @PutMapping
    public ResponseEntity<Object> updateUser(@Valid @RequestBody User newUser) {
        User updatedUser = userService.updateUser(newUser);
        if (updatedUser != null) {
            return ResponseEntity.ok(updatedUser);
        } else {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    @PutMapping("/{id}/friends/{friendId}")
    public ResponseEntity<String> addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        Objects.requireNonNull(id, "User ID cannot be null");
        Objects.requireNonNull(friendId, "Friend ID cannot be null");

        try {
            userService.addFriend(id, friendId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\":\"Error adding friend\"}");
        }

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    public ResponseEntity<Boolean> removeFriend(@PathVariable Long userId, @PathVariable Long friendId) {
        Objects.requireNonNull(userId, "Идентификатор пользователя не может быть null");
        Objects.requireNonNull(friendId, "Идентификатор друга не может быть null");
        boolean removeFriend = userService.removeFriend(userId, friendId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(removeFriend);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeUser(@PathVariable long id) {

        try {
            userService.removeUser(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (ValidationException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        Objects.requireNonNull(id, "Идентификатор пользователя не может быть null");
        return userService.getUserById(id);
    }
}