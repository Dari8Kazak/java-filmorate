package ru.yandex.practicum.filmorate.controller;

import ch.qos.logback.classic.Logger;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/films")
public class FilmController {

    public final FilmService filmService;
    public Logger log;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<Film> getAllFilms() {
        return filmService.getAllFilms();
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") @Positive int count) {
        return filmService.getPopularFilms(count);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Film> getFilm(@PathVariable Long id) {
        Film film = filmService.getFilmById(id);
        return new ResponseEntity<>(film, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Film> createFilm(@Valid @RequestBody Film film) {
        Objects.requireNonNull(film, "film не должен быть null");
        Film createdFilm = filmService.createFilm(film);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdFilm);
    }

    @PutMapping
    public ResponseEntity<Film> updateFilm(@Valid @RequestBody Film newFilm) {
        Objects.requireNonNull(newFilm, "newFilm не должен быть null");
        return new ResponseEntity<>(filmService.updateFilm(newFilm), HttpStatus.OK);
    }

    @PutMapping("/{filmId}/like/{userId}")
    public ResponseEntity<Film> likeFilm(@PathVariable Long filmId, @PathVariable Long userId) {
        Objects.requireNonNull(filmId, "id не должен быть null");
        Objects.requireNonNull(userId, "userId не должен быть null");
        filmService.addLikeFilm(filmId, userId);
        Film updatedFilm = filmService.getFilmById(filmId);
        return ResponseEntity.ok(updatedFilm);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public ResponseEntity<Boolean> removeLike(@PathVariable Long filmId, @PathVariable Long userId) {
        boolean removed = filmService.removeLikeFilm(filmId, userId);
        return new ResponseEntity<>(removed, HttpStatus.OK);
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
}