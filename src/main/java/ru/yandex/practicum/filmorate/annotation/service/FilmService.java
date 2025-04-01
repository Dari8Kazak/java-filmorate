package ru.yandex.practicum.filmorate.annotation.service;

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
    private final Map<Long, Set<Long>> likes;

    public Film createFilm(Film film) {
        Objects.requireNonNull(film, "film не должен быть null");
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        Objects.requireNonNull(film, "film не должен быть null");
        return filmStorage.updateFilm(film);
    }

    public Film getFilmById(Long filmId) {
        Objects.requireNonNull(filmId, "filmId не должен быть null");
        return filmStorage.getFilmById(filmId);
    }

    public void deleteFilm(Long filmId) {
        Objects.requireNonNull(filmId, "filmId не должен быть null");
        filmStorage.deleteFilm(filmId);
    }

    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public void addLikeFilm(Long filmId, Long userId) {
        Objects.requireNonNull(filmId, "filmId не должен быть null");
        Objects.requireNonNull(userId, "userId не должен быть null");

        Film film = filmStorage.getFilmById(filmId);
        User user = userStorage.getUserById(userId);

        if (likes.containsValue(userId)) {
            throw new ValidationException("Lke", "userId", "Пользователь уже ставил лайк");
        }
        film.addLike(userId);
        filmStorage.updateFilm(film);
    }

    public boolean removeLikeFilm(Long filmId, Long userId) {
        Objects.requireNonNull(filmId, "filmId не должен быть null");
        Objects.requireNonNull(userId, "userId не должен быть null");

        Film film = filmStorage.getFilmById(filmId);
        User user = userStorage.getUserById(userId);

        if (!film.getLikes().contains(userId)) {
            throw new ValidationException("Lke", "userId", "У фильма нет лайков");
        }
        film.removeLike(userId);
        filmStorage.updateFilm(film);
        return false;
    }

    public Set<Long> getLikes(Long filmId) {
        return likes.getOrDefault(filmId, Collections.emptySet());
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getAllFilms().stream()
                .filter(film -> film.getLikes() != null && !film.getLikes().isEmpty())
                .sorted((film1, film2) -> Integer.compare(film2.getLikes().size(), film1.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }
}