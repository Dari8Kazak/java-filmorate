package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {

    @Autowired
    public final FilmService filmService;

    @GetMapping
    public Collection<Film> getAllFilms() {
        log.info("Запрошены все фильмы");
        return filmService.getAllFilms();
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") @Positive int count) {
        log.info("Запрошено {} самых популярных фильмов", count);
        log.info("Запрошено {} самых популярных фильмов", filmService.getPopularFilms(count));
        return filmService.getPopularFilms(count);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Set<Long>> getLikesFilm(@PathVariable Long id) {
        Objects.requireNonNull(id, "id не должен быть null");
        Set<Long> likes = filmService.getFilmById(id).getLikes();
        return new ResponseEntity<>(likes, HttpStatus.OK);
    }

    @GetMapping("/{id}/film")
    public ResponseEntity<Film> getFilm(@PathVariable Long id) {
        Objects.requireNonNull(id, "id не должен быть null");
        Film film = filmService.getFilmById(id);
        return new ResponseEntity<>(film, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Film> createFilm(@Valid @RequestBody Film film) {
        Objects.requireNonNull(film, "film не должен быть null");
        Film createdFilm = filmService.createFilm(film);
        log.info("Film created: {}", createdFilm);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdFilm);
    }

    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity<Film> likeFilm(@PathVariable Long id, @PathVariable Long userId) {
        Objects.requireNonNull(id, "id не должен быть null");
        Objects.requireNonNull(userId, "userId не должен быть null");
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
        Objects.requireNonNull(id, "id не должен быть null");
        try {
            filmService.deleteFilm(id);
            log.info("Film deleted: {}", id);
        } catch (ValidationException e) {
            log.error(e.getMessage());
        }
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity<Boolean> removeLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Получен запрос на удаление лайка фильму с ID={} от пользователя ID={}", id, userId);
        boolean removed = filmService.removeLikeFilm(id, userId);
        return new ResponseEntity<>(removed, HttpStatus.OK);
    }
}