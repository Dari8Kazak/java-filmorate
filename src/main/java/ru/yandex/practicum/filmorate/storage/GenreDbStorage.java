
package ru.yandex.practicum.filmorate.storage;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.util.*;

@Slf4j
@AllArgsConstructor
@Repository
public class GenreDbStorage implements GenreStorage {
    private JdbcTemplate jdbc;

    public List<Genre> getByIds(List<Long> ids) {
        log.info("Начало подготовки списка жанров по их ID");
        if (ids == null || ids.isEmpty()) {
            log.info("Список id пуст или равен null. Возвращён пустой список.");
            return Collections.emptyList();
        }
        String placeholders = String.join(",", Collections.nCopies(ids.size(), "?"));
        final String GET_GENRES_BY_IDS = "select genre_id, name from genres where genre_id IN (" + placeholders + ")";

        List<Genre> genres = jdbc.query(GET_GENRES_BY_IDS, (rs, rowNum) -> {
            Genre genre = new Genre();
            genre.setId(rs.getLong("genre_id"));
            genre.setName(rs.getString("name"));
            return genre;
        }, ids.toArray());

        log.info("Завершение подготовки списка жанров.");
        return genres;
    }

    @Override
    public List<Genre> getAllGenres() {
        log.info("Получен запрос на получение списка всех жанров");

        final String GET_ALL_GENRES = "select * from genres;";

        List<Genre> genres = jdbc.query(GET_ALL_GENRES, (ResultSet rs) -> {
            Map<Long, Genre> genreMap = new LinkedHashMap<>();

            while (rs.next()) {
                Long genreId = rs.getLong("genre_id");
                Genre genre = genreMap.get(genreId);

                if (genre == null) {
                    genre = new Genre();
                    genre.setId(genreId);
                    genre.setName(rs.getString("name"));

                    genreMap.put(genreId, genre);
                }
            }
            return new ArrayList<>(genreMap.values());
        });

        log.info("Завершение получения списка жанров.");
        return genres;
    }

    @Override
    public Genre getGenreById(Long id) {
        log.info("Получен запрос на получение жанра с ID {}", id);

        final String FIND_GENRE_BY_ID = "SELECT * FROM genres WHERE genre_id = ?;";

        try {
            Genre genre = jdbc.queryForObject(FIND_GENRE_BY_ID, (ResultSet rs, int rowNum) -> {
                Genre g = new Genre();
                g.setId(rs.getLong("genre_id"));
                g.setName(rs.getString("name"));
                return g;
            }, id);

            log.info("Жанр с ID {} успешно найден: {}", id, genre);
            return genre;
        } catch (EmptyResultDataAccessException e) {
            throw new ValidationException("Жанр с ID " + id + " не найден");
        }
    }
}