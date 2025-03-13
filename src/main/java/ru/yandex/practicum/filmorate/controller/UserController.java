package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.annotation.service.InMemoryUserServiceImpl;
import ru.yandex.practicum.filmorate.annotation.service.UserService;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService = new InMemoryUserServiceImpl();

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        try {
            userService.createUser(user);
            log.info("User created: {}", user);
        } catch (ValidationException e) {
            log.error(e.getMessage());
        }
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User newUser) {
        return userService.updateUser(newUser);
    }

    @GetMapping
    public Collection<User> findAllUsers() {
        return userService.findAll();
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable long id) {
        try {
            userService.deleteUser(id);
        } catch (ValidationException e) {
            log.error(e.getMessage());
        }
    }
}