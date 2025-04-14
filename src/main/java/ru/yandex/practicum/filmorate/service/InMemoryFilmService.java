package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.RatingMPA;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InMemoryFilmService implements FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;

    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getPopularFilms(count);
    }

    @SneakyThrows
    public Film getFilmById(Long filmId) {
        return filmStorage.getFilmById(filmId)
                .orElseThrow(() -> new Exception("Фильм с ID " + filmId + " не найден"));
    }

    public Film createFilm(Film film) {
        checkForRelatedData(film);
        return filmStorage.createFilm(film);
    }

    @SneakyThrows
    public Film updateFilm(Film newFilm) {
        checkForRelatedData(newFilm);
        return filmStorage.updateFilm(newFilm);
    }

    @SneakyThrows
    public void addLikeFilm(Long filmId, Long userId) {
        filmStorage.getFilmById(filmId)
                .orElseThrow(() -> new Exception("Фильм с ID " + filmId + " не найден"));

        if (!userStorage.containsUserById(userId)) {
            throw new Exception("Пользователь с ID " + userId + " не найден");
        }

        if (filmStorage.isLikeExist(filmId, userId)) {
            throw new ValidationException("Пользователь уже поставил лайк этому фильму");
        }

        filmStorage.addLike(filmId, userId);
    }

    @SneakyThrows
    public boolean removeLikeFilm(Long filmId, Long userId) {
        filmStorage.getFilmById(filmId)
                .orElseThrow(() -> new Exception("Фильм с id = " + filmId + " не найден"));

        if (!userStorage.containsUserById(userId)) {
            throw new Exception("Пользователь с id = " + userId + " не найден");
        }

        if (!filmStorage.isLikeExist(filmId, userId)) {
            throw new ValidationException("Лайк не существует");
        }

        filmStorage.removeLike(filmId, userId);
        return true;
    }

    public void deleteFilm(Long filmId) {
        filmStorage.deleteFilm(filmId);
    }

    public void checkForRelatedData(Film film) {
        if (film.getMpa() != null && film.getMpa().getId() != null) {
            RatingMPA ratingMPA = mpaStorage.getMpaById(film.getMpa().getId());
            if (ratingMPA == null) {
                throw new ValidationException("MPA с id " + film.getMpa().getId() + " не найден");
            }

            film.getMpa().setName(ratingMPA.getName());
        }

        if (film.getGenres() != null) {
            film.getGenres().forEach(genre -> {
                Genre existingGenre = genreStorage.getGenreById(genre.getId());
                if (existingGenre == null) {
                    throw new ValidationException("Жанр с id " + genre.getId() + " не найден");
                }
                genre.setName(existingGenre.getName());
            });
        }
    }
}
