package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.annotation.service.UserService;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.FriendDto;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserStorage userStorage;
    private final UserService userService;

    @GetMapping
    public Collection<User> findAllUsers() {
        return userStorage.findAll();
    }

    @GetMapping("/{userId}/friends")
    public ResponseEntity<List<FriendDto>> findAllFriends(@PathVariable Long userId) {
        List<FriendDto> allFriends = userService.findAllFriend(userId);
        return new ResponseEntity<>(allFriends, HttpStatus.OK);
    }

    @GetMapping("/{userId}/friends/common/{friendId}")
    public ResponseEntity<List<FriendDto>> findCommonFriends(@PathVariable Long userId, @PathVariable Long friendId) {
        List<FriendDto> commonFriends = userService.findCommonFriends(userId, friendId);
        return new ResponseEntity<>(commonFriends, HttpStatus.OK);
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        try {
            userStorage.createUser(user);
            log.info("User created: {}", user);
        } catch (ValidationException e) {
            log.error(e.getMessage());
        }
        return user;
    }

    @PutMapping
    public ResponseEntity<User> updateUser(@Valid @RequestBody User newUser) {
        User updateUser = userStorage.updateUser(newUser);
        return new ResponseEntity<>(updateUser, HttpStatus.OK);
    }

    @PutMapping("/{userId}/friends/{friendId}")
    public ResponseEntity<Boolean> addFriend(@PathVariable Long userId, @PathVariable Long friendId) {
        boolean created = userService.addFriends(userId, friendId);
        return new ResponseEntity<>(created, HttpStatus.OK);
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    public ResponseEntity<Boolean> deleteFriend(@PathVariable Long userId, @PathVariable Long friendId) {
        boolean deleted = userService.deleteFriendById(userId, friendId);
        return new ResponseEntity<>(deleted, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable long id) {
        try {
            userStorage.deleteUser(id);
        } catch (ValidationException e) {
            log.error(e.getMessage());
        }
    }
}