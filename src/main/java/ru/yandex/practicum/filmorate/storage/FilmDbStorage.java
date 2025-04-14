package ru.yandex.practicum.filmorate.storage;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.RatingMPA;

import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Repository
public class FilmDbStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();


    private JdbcTemplate jdbc;

    @Override
    public Collection<Film> getAllFilms() {
        final String GET_ALL = """
                SELECT
                    f.film_id AS id_film,
                    f.name AS film_name,
                    f.description AS film_description,
                    f.release_date AS release_date,
                    f.duration AS film_duration,
                    rm.rating_mpa_id AS id_rating_mpa,
                    rm.name AS name_rating_mpa,
                    (
                        SELECT COUNT(*)
                        FROM likes l
                        WHERE l.film_id = f.film_id
                    ) AS likes_count,
                    fg.genre_id AS id_genre,
                    g.name AS name_genre
                FROM films AS f
                LEFT JOIN rating_mpa AS rm ON f.rating_mpa_id = rm.rating_mpa_id
                LEFT JOIN film_genres AS fg ON f.film_id = fg.film_id
                LEFT JOIN genres AS g ON fg.genre_id = g.genre_id
                ORDER BY id_film""";

        return jdbc.query(GET_ALL, (ResultSet rs) -> {
            Map<Integer, Film> films = new LinkedHashMap<>();

            while (rs.next()) {
                int filmId = rs.getInt("id_film");
                Film film = films.get(filmId);
                if (film == null) {
                    film = extractFilm(rs);
                    films.put(filmId, film);
                }
                addGenreToFilm(rs, film);
            }
            log.info("Список всех фильмов подготовлен");
            return new ArrayList<>(films.values());
        });
    }


    @Override
    public List<Film> getPopularFilms(int count) {
        final String GET_POPULAR_FILMS = """
                SELECT
                    f.film_id AS id_film,
                    f.name AS film_name,
                    f.description AS film_description,
                    f.release_date AS release_date,
                    f.duration AS film_duration,
                    rm.rating_mpa_id AS id_rating_mpa,
                    rm.name AS name_rating_mpa,
                    (
                        SELECT COUNT(*)
                        FROM likes l
                        WHERE l.film_id = f.film_id
                    ) AS likes_count
                FROM films AS f
                LEFT JOIN rating_mpa AS rm ON f.rating_mpa_id = rm.rating_mpa_id
                ORDER BY likes_count DESC
                LIMIT ?""";

        Map<Integer, Film> films = jdbc.query(GET_POPULAR_FILMS,
                ps -> ps.setInt(1, count), // Устанавливаем параметр в PreparedStatement
                rs -> {
                    Map<Integer, Film> result = new LinkedHashMap<>();
                    while (rs.next()) {
                        int filmId = rs.getInt("id_film");
                        Film film = extractFilm(rs);
                        result.put(filmId, film);
                    }
                    return result;
                });

        if (films == null || films.isEmpty()) {
            return new ArrayList<>();
        }

        List<Integer> filmIds = new ArrayList<>(films.keySet());

        String filmIdsPlaceholders = filmIds.stream()
                .map(id -> "?")
                .collect(Collectors.joining(","));

        final String GET_GENRES_FOR_FILMS = """
                SELECT fg.film_id, g.genre_id AS id_genre, g.name AS name_genre
                FROM film_genres fg
                JOIN genres g ON fg.genre_id = g.genre_id
                WHERE fg.film_id IN (%s)""".formatted(filmIdsPlaceholders);

        // Передаём список параметров через PreparedStatementSetter
        jdbc.query(GET_GENRES_FOR_FILMS,
                ps -> {
                    for (int i = 0; i < filmIds.size(); i++) {
                        ps.setInt(i + 1, filmIds.get(i)); // Устанавливаем каждый ID как параметр
                    }
                },
                rs -> {
                    while (rs.next()) {
                        int filmId = rs.getInt("film_id");
                        Film filmForGenresAdding = films.get(filmId);
                        if (films.containsKey(filmId)) {
                            addGenreToFilm(rs, filmForGenresAdding);
                        }
                    }
                });

        log.info("Список наиболее популярных фильмов подготовлен");
        return new ArrayList<>(films.values());
    }

    @Override
    public Optional<Film> getFilmById(Long id) {
        final String FIND_BY_ID = """
                SELECT f.film_id,
                       f.name AS film_name,
                       f.description AS film_description,
                       f.release_date AS release_date,
                       f.duration AS film_duration,
                       rm.rating_mpa_id AS id_rating_mpa,
                       rm.name AS name_rating_mpa,
                       (SELECT COUNT(*) FROM likes l WHERE l.film_id = f.film_id) AS likes_count,
                       g.genre_id AS id_genre,
                       g.name AS name_genre
                FROM films AS f
                LEFT JOIN rating_mpa AS rm ON f.rating_mpa_id = rm.rating_mpa_id
                LEFT JOIN film_genres AS fg ON f.film_id = fg.film_id
                LEFT JOIN genres AS g ON fg.genre_id = g.genre_id
                WHERE f.film_id = ?;
                """;

        return jdbc.query(FIND_BY_ID, (ResultSet rs) -> {
            Film film = null;
            while (rs.next()) {
                if (film == null) {
                    film = extractFilm(rs);
                }
                addGenreToFilm(rs, film);
            }
            log.info("Подготовлен Optional, который, возможно, содержит фильм с id = {}", id);
            return Optional.ofNullable(film);
        }, id);
    }

    @Override
    public Film createFilm(Film film) {
        final String CREATE_FILM = "INSERT INTO films (name, description, release_date, duration, rating_mpa_id) " +
                "VALUES (?, ?, ?, ?, ?);";
        final GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(CREATE_FILM, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setLong(4, film.getDuration());
            if (film.getMpa() != null) {
                ps.setInt(5, Math.toIntExact(film.getMpa().getId()));
            } else {
                ps.setNull(5, Types.INTEGER);
            }
            return ps;
        }, keyHolder);

        Long generatedId = null;

        if (keyHolder.getKeys() != null && keyHolder.getKeys().containsKey("film_id")) {
            generatedId = ((Number) keyHolder.getKeys().get("film_id")).longValue();
        }

        if (generatedId != null) {
            film.setId(generatedId);
        } else {
            throw new RuntimeException("Не удалось получить сгенерированный ID для фильма");
        }

        insertGenres(film);

        log.info("Добавление фильма: {} - закончено, присвоен id: {}", film, film.getId());
        return film;
    }

    @SneakyThrows
    @Override
    public Film updateFilm(Film newFilm) {
           getFilmById(newFilm.getId())
                .orElseThrow(() -> new Exception("Фильм с ID " + newFilm.getId() + " не найден"));

        final String UPDATE_FILM = "update films set " +
                "name = ?, description = ?, release_date = ?, duration = ?, rating_mpa_id = ? " +
                "where film_id = ?;";
        Long newFilmId = newFilm.getId();
        Long ratingMPAId = (newFilm.getMpa() != null && newFilm.getMpa().getId() != null)
                ? newFilm.getMpa().getId()
                : null;
        jdbc.update(UPDATE_FILM,
                newFilm.getName(),
                newFilm.getDescription(),
                newFilm.getReleaseDate(),
                newFilm.getDuration(),
                ratingMPAId,
                newFilmId
        );

        final String DELETE_ALL_OLD_GENRES = "delete from film_genres \n" +
                "where film_id = ?;";
        jdbc.update(DELETE_ALL_OLD_GENRES, newFilmId);

        insertGenres(newFilm);

        final Film updatedFilm = getFilmById(newFilmId)
                .orElseThrow(() -> new Exception("Фильм с ID " + newFilmId + " не найден"));

        log.info("Обновление фильма: {} - закончено.", newFilm);
        return updatedFilm;
    }

    @Override
    public void addLike(Long filmId, Long userId) {

        final String INSERT_LIKE = "MERGE INTO likes (user_id, film_id) VALUES (?, ?);";
        int rowsAffected = jdbc.update(INSERT_LIKE, userId, filmId);

        if (rowsAffected > 0) {
            log.info("Пользователь с ID {} поставил лайк фильму с ID {}", userId, filmId);
        } else {
            log.warn("Не удалось добавить like для userId: {} и filmId: {}", userId, filmId);
        }
    }

    @Override
    public boolean isLikeExist(Long filmId, Long userId) {
        final String GET_LIKE = "SELECT COUNT(*) FROM likes WHERE film_id = ? AND user_id = ?";
        Integer likeCount = jdbc.queryForObject(GET_LIKE, Integer.class, filmId, userId);
        return likeCount != null && likeCount > 0;
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        final String DELETE_LIKE = "delete from likes \n" +
                "where (user_id, film_id)  = (?,?);";
        int rowsAffected = jdbc.update(DELETE_LIKE, userId, filmId);
        if (rowsAffected > 0) {
            log.info("Пользователь с ID {} удалил лайк фильму с ID {}", userId, filmId);
        } else {
            log.warn("Пользователю с ID {} не удалось удалить лайк фильму с ID {}", userId, filmId);
        }
    }

    @Override
    public void deleteFilm(Long filmId) {

        if (!films.containsKey(filmId)) {
            log.info("filmId: {} -> не найден", filmId);
        }
        films.remove(filmId);
    }

    private void insertGenres(Film film) {
        log.info("Начало добавления жанров фильма с id = {} в таблицу film_genres.", film.getId());

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            final String INSERT_GENRE = "MERGE INTO film_genres (film_id, genre_id) KEY (film_id, genre_id)" +
                    " VALUES (?, ?)";
            List<Object[]> batchArgs = new ArrayList<>();
            for (Genre genre : film.getGenres()) {
                batchArgs.add(new Object[]{film.getId(), genre.getId()});
            }
            jdbc.batchUpdate(INSERT_GENRE, batchArgs);
        }
        log.info("Конец добавления жанров фильма с id = {} в таблицу film_genres.", film.getId());

    }

    public Film extractFilm(ResultSet rs) {
        try {
            Film film = new Film();
            film.setId(rs.getLong("film_id"));
            film.setName(rs.getString("film_name"));
            film.setDescription(rs.getString("film_description"));
            film.setDuration(rs.getLong("film_duration"));

            Date releaseDate = rs.getDate("release_date");
            if (releaseDate != null) {
                film.setReleaseDate(releaseDate.toLocalDate());
            }

            Long ratingId = rs.getLong("id_rating_mpa");
            String ratingName = rs.getString("name_rating_mpa");
            if (!rs.wasNull()) {
                RatingMPA rating = new RatingMPA(ratingId, ratingName);
                film.setMpa(rating);
            }
            film.setGenres(new LinkedHashSet<>());

            return film;
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при извлечении фильма из ResultSet", e);
        }
    }

    private void addGenreToFilm(ResultSet rs, Film film) {
        try {
            int genreId = rs.getInt("id_genre");
            String genreName = rs.getString("name_genre");
            if (!rs.wasNull()) {
                Genre genre = new Genre();
                genre.setId((long) genreId);
                genre.setName(genreName);
                film.getGenres().add(genre);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при добавлении жанра к фильму", e);
        }
    }
}