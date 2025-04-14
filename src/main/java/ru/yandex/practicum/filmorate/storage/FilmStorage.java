package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Film createFilm(Film film);

    Collection<Film> getAllFilms();

    void deleteFilm(Long id);

    Film updateFilm(Film newFilm);

    Optional<Film> getFilmById(Long id);

    List<Film> getPopularFilms(int count);

    void addLike(Long filmId, Long userId);

    void removeLike(Long filmId, Long userId);

    boolean isLikeExist(Long filmId, Long userId);

}