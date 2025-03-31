package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Set;

public interface FilmStorage {

    Film createFilm(Film film);

    Film updateFilm(Film film);

    void deleteFilm(Long id);

    Film getFilmById(Long filmId);

    Collection<Film> getAllFilms();

    Set<Long> getLikes(Long filmId);

}