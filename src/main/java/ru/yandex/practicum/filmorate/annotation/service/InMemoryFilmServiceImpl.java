package ru.yandex.practicum.filmorate.annotation.service;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class InMemoryFilmServiceImpl implements FilmService {

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
            throw new ValidationException("Film is null");
        }
        if (!films.containsKey(film.getId())) {
            throw new ValidationException("Film not found");
        }
        log.info("Updating film {}", film.getId());
        films.computeIfPresent(film.getId(), (id, oldUser) -> film);

        return films.get(film.getId());
    }

    @Override
    public void deleteFilm(Long id) {
        if (id == null) {
            throw new ValidationException("Film is null");
        }
        if (films.containsKey(id)) {
            films.remove(id);
        } else {
            throw new ValidationException("Film not found");
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
}