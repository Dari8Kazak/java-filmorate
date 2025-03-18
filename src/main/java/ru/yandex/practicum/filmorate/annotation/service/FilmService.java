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
    private final Map<Long, Set<Long>> likes = new HashMap<>();

    public List<Film> findAllFilms() {
        return filmStorage.findAll().stream().toList();
    }

    public void deleteFilm(Long id) {
        filmStorage.deleteFilm(id);
    }

    public boolean addLikeFilm(Long filmId, Long userId) {
        Film film = getFilmById(filmId);
        User user = userStorage.findById(userId);
        boolean existsLikes = existsLikes(film.getId(), user.getId());
        if (existsLikes) {
            throw new ValidationException("Lke", "userId", "Пользователь уже ставил лайк");
        }

        return likes.computeIfAbsent(filmId, k -> new HashSet<>()).add(userId);
    }


    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public boolean removeLikeFilm(Long filmId, Long userId) {
        Film film = getFilmById(filmId);
        User user = userStorage.findById(userId);
        boolean existsLikes = existsLikes(film.getId(), user.getId());
        if (!existsLikes) {
            throw new ValidationException("Lke", "userId", " фильма нет лайков");
        }
        return removeLike(film.getId(), userId);
    }

    private boolean removeLike(Long filmId, Long userId) {
        Set<Long> userLikes = getLikes(filmId);
        if (userLikes != null) {
            userLikes.remove(userId);
            if (userLikes.isEmpty()) {
                likes.remove(filmId);
            }
            return true;
        }
        return false;
    }

    public Film getFilmById(Long id) {
        return filmStorage.getFilmById(id);
    }

    public Set<Long> getLikes(Long filmId) {
        return likes.get(filmId);
    }

    private boolean existsLikes(Long filmId, Long userId) {
        return likes.containsKey(filmId) && likes.get(filmId).contains(userId);
    }

    public List<Film> findPopularFilms(int count) {
        int validCount = count <= 0 ? 10 : count;
        return findAllFilms().stream()
                .sorted(Comparator.comparingInt((Film film) -> likes.getOrDefault(film.getId(), Collections.emptySet()).size()).reversed())
                .limit(validCount)
                .toList();
    }

    public Film createFilm(Film film) {
        return filmStorage.createFilm(film);
    }
}