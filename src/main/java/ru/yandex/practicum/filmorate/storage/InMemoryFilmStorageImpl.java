package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Slf4j
@Component
public class InMemoryFilmStorageImpl implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private final Map<Long, Set<Long>> likes = new HashMap<>();

    @Override
    public Film createFilm(Film film) {
        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (!films.containsKey(film.getId())) {
            throw new NotFoundException("Film not found");
        }
        log.info("Updating film {}", film.getId());
        films.computeIfPresent(film.getId(), (id, oldUser) -> film);
        return films.get(film.getId());
    }

    public Film getFilmById(Long filmId) {
        Film film = films.get(filmId);
        if (film == null) {
            throw new NotFoundException("Film not found for filmId " + filmId);
        }
        return films.get(filmId);
    }

    @Override
    public void deleteFilm(Long filmId) {
        if (!films.containsKey(filmId)) {
            throw new ValidationException("Film", "id", "Film is not found filmId: " + filmId);
        }
        films.remove(filmId);
    }

    @Override
    public Collection<Film> getAllFilms() {
        return films.values();
    }

    private Long getNextId() {
        long currentId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentId;
    }
}