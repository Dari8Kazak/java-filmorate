package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.RatingMPA;
import ru.yandex.practicum.filmorate.service.InMemoryMpaService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mpa")
public class MpaController {

    private final InMemoryMpaService mpaService;

    @GetMapping
    public List<RatingMPA> getAllMpa() {
        return mpaService.getAllMpa();
    }

    @GetMapping("/{id}")
    public RatingMPA getMpaById(@PathVariable Long id) {
        return mpaService.getMpaById(id);
    }
}