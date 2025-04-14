package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmService {

    Collection<Film> getAllFilms();

    List<Film> getPopularFilms(int count);

    Film getFilmById(Long filmId);

    Film createFilm(Film film);

    Film updateFilm(Film newFilm);

    void addLikeFilm(Long filmId, Long userId);

    boolean removeLikeFilm(Long filmId, Long userId);

    void deleteFilm(Long filmId);

    void checkForRelatedData(Film film);
}
