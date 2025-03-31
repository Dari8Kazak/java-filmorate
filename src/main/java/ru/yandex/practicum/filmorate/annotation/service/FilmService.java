package ru.yandex.practicum.filmorate.annotation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final Map<Long, Set<Long>> likes;

    public Film createFilm(Film film) {
        if (film == null) {
            throw new ValidationException("Film", "film", "Film is null");
        }
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        if (film == null) {
            throw new ValidationException("Film", "film", "Film is null");
        }
        return filmStorage.updateFilm(film);
    }

    public Film getFilmById(Long filmId) {
        if (filmId == null) {
            throw new ValidationException("Film", "id", "Film is null");
        }
        return filmStorage.getFilmById(filmId);
    }

    public void deleteFilm(Long filmId) {
        if (filmId == null) {
            throw new ValidationException("Film", "id", "Film is null");
        }
        filmStorage.deleteFilm(filmId);
    }

    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public void addLikeFilm(Long filmId, Long userId) {
        if (filmId == null) {
            throw new IllegalArgumentException("Идентификатор фильма не может быть null");
        }
        if (userId == null) {
            throw new IllegalArgumentException("Идентификатор пользователя не может быть null");
        }
        Film film = filmStorage.getFilmById(filmId);
        User user = userStorage.getUserById(userId);

        if (likes.containsValue(userId)) {
            throw new ValidationException("Lke", "userId", "Пользователь уже ставил лайк");
        }
        film.addLike(userId);
        filmStorage.updateFilm(film);
    }

    public boolean removeLikeFilm(Long filmId, Long userId) {
        if (filmId == null) {
            throw new IllegalArgumentException("Идентификатор фильма не может быть null");
        }
        if (userId == null) {
            throw new IllegalArgumentException("Идентификатор пользователя не может быть null");
        }
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

    public List<Film> getMostPopularFilms(int count) {
        return filmStorage.getAllFilms().stream()
                .sorted(Comparator.comparingInt(f -> filmStorage.getLikes(f.getId()).size()))
                .limit(count)
                .toList();
    }
}