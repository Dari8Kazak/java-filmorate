package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.annotation.service.FilmService;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @GetMapping
    public Collection<Film> findAllFilms() {
        return filmService.findAllFilms();
    }

    @GetMapping("/popular")
    public ResponseEntity<List<Film>> findPopularFilms(@RequestParam(defaultValue = "10") int count) {
        List<Film> popularFilms = filmService.findPopularFilms(count);
        return new ResponseEntity<>(popularFilms, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Set<Long>> getLikesFilm(@PathVariable Long id) {
        Set<Long> likes = filmService.getLikes(id);
        return new ResponseEntity<>(likes, HttpStatus.OK);
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        try {
            filmService.createFilm(film);
            log.info("Film created: {}", film);
        } catch (ValidationException e) {
            log.error(e.getMessage());
        }
        return film;
    }

    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity<Boolean> likeFilm(@PathVariable Long id, @PathVariable Long userId) {
        boolean isLikeFilm = filmService.addLikeFilm(id, userId);
        return new ResponseEntity<>(isLikeFilm, HttpStatus.OK);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film newFilm) {
        return filmService.updateFilm(newFilm);
    }

    @DeleteMapping("/{id}")
    public void deleteFilm(@Valid @PathVariable Long id) {
        try {
            filmService.deleteFilm(id);
            log.info("Film deleted: {}", id);
        } catch (ValidationException e) {
            log.error(e.getMessage());
        }
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity<Boolean> removeLike(@PathVariable Long id, @PathVariable Long userId) {
        boolean removed = filmService.removeLikeFilm(id, userId);
        return new ResponseEntity<>(removed, HttpStatus.OK);
    }

}