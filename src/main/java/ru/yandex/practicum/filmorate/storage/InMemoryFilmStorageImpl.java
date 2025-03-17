package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorageImpl implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    public Film createFilm(Film film) {
        if (film.getId() == null) {
            film.setId(getNextId());
        }
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (film == null) {
            throw new ValidationException("Film", "film", "Film is null");
        }
        if (!films.containsKey(film.getId())) {
            throw new NotFoundException("Film not found");
        }
        log.info("Updating film {}", film.getId());
        films.computeIfPresent(film.getId(), (id, oldUser) -> film);

        return films.get(film.getId());
    }

    @Override
    public void deleteFilm(Long id) {
        if (id == null) {
            throw new ValidationException("Film", "id", "Film is null");
        }
        if (films.containsKey(id)) {
            films.remove(id);
        } else {
            throw new NotFoundException("Film not found");
        }
    }

    private Long getNextId() {
        long currentId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentId;
    }

    public Film getFilmById(Long filmId) {
        if (filmId == null) {
            throw new ValidationException("Film", "id", "Film is null");
        }
        if (!films.containsKey(filmId)) {
            throw new NotFoundException("Film not found filmId " + filmId);
        }
        return films.get(filmId);
    }
}