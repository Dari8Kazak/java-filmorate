package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorageImpl;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryFilmServiceImplTest {
    private FilmStorage filmStorage;
    private Film film;

    @BeforeEach
    void setUp() {
        filmStorage = new InMemoryFilmStorageImpl();
        film = Film.builder()
                .id(1L)
                .name("Film 1")
                .duration(Duration.ofMinutes(30).toMinutes())
                .description("Film 1 description")
                .releaseDate(LocalDate.of(2000, 10, 10))
                .build();
    }

    @DisplayName("Успешное создание фильма")
    @Test
    void testCreateFilm() {
        filmStorage.createFilm(film);
        Collection<Film> allFilms = filmStorage.findAll();

        assertTrue(allFilms.contains(film));
        assertEquals(1, allFilms.size());
    }

    @DisplayName("Успешное обновление фильма")
    @Test
    void testUpdateFilm() {
        film.setId(1L);
        filmStorage.createFilm(film);
        Film updatedFilm = Film.builder()
                .id(1L)
                .name("Film 2")
                .duration(Duration.ofMinutes(30).toMinutes())
                .description("Film 2 description")
                .releaseDate(LocalDate.of(2024, 10, 10))
                .build();
        Film result = filmStorage.updateFilm(updatedFilm);

        assertNotNull(result);
        assertEquals(updatedFilm.getId(), result.getId());
        assertEquals(updatedFilm.getName(), result.getName());
        assertEquals(updatedFilm.getDescription(), result.getDescription());
    }

    @DisplayName("Ошибка обновление фильма")
    @Test
    void testUpdateFilmFailed() {
        Exception exception = assertThrows(NotFoundException.class, () -> filmStorage.updateFilm(film));
        assertEquals("Film not found", exception.getMessage());
    }

    @DisplayName("Удаление фильма")
    @Test
    void deleteFilm() {
        filmStorage.createFilm(film);
        Collection<Film> all = filmStorage.findAll();
        assertTrue(all.contains(film));
        filmStorage.deleteFilm(film.getId());
        assertTrue(all.isEmpty());
    }
}