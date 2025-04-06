package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Film createFilm(Film film) {
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public Film getFilmById(Long filmId) {
        return filmStorage.getFilmById(filmId);
    }

    public void deleteFilm(Long filmId) {
        filmStorage.deleteFilm(filmId);
    }

    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public void addLikeFilm(Long filmId, Long userId) {

        Film film = filmStorage.getFilmById(filmId);
        User user = userStorage.getUserById(userId);

        if (film.getLikes().contains(userId)) {
            throw new ValidationException("Lke", "userId", "Пользователь уже ставил лайк");
        }
        film.addLike(userId);
        filmStorage.updateFilm(film);
    }

    public boolean removeLikeFilm(Long filmId, Long userId) {

        Film film = filmStorage.getFilmById(filmId);
        User user = userStorage.getUserById(userId);

        if (!film.getLikes().contains(userId)) {
            throw new ValidationException("Lke", "userId", "У фильма нет лайков");
        }
        film.removeLike(userId);
        filmStorage.updateFilm(film);
        return false;
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getAllFilms().stream()
                .sorted((film1, film2) -> Integer.compare(film2.getLikes().size(), film1.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }
}