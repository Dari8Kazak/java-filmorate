package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public final FilmService filmService;

    @GetMapping
    public Collection<Film> findAllFilms() {
        return filmService.findAllFilms();
    }

    @GetMapping("/popular")
    public ResponseEntity<List<Film>> findPopularFilms(@RequestParam (defaultValue = "10") @Positive int count) {
        List<Film> popularFilms = filmService.findPopularFilms(count);
        return new ResponseEntity<>(popularFilms, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Set<Long>> getLikesFilm(@PathVariable Long id) {
        Set<Long> likes = filmService.getLikes(id);
        return new ResponseEntity<>(likes, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Film> createFilm(@Valid @RequestBody Film film) {
            Film createdFilm = filmService.createFilm(film);
            log.info("Film created: {}", createdFilm);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdFilm);
    }

    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity<Film> likeFilm(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Получен запрос на установку лайка фильму с ID={} от пользователя ID={}", id, userId);
        filmService.addLikeFilm(id, userId);
        Film updatedFilm = filmService.getFilmById(id);
        return ResponseEntity.ok(updatedFilm);
    }

    @PutMapping
            public ResponseEntity<Film> updateFilm(@Valid @RequestBody Film newFilm) {
            return new ResponseEntity<>(filmService.updateFilm(newFilm), HttpStatus.OK);
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