package ru.yandex.practicum.filmorate.storage;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.RatingMPA;

import java.util.*;

@Slf4j
@AllArgsConstructor
@Repository
public class MpaDbStorage implements MpaStorage {

    private JdbcTemplate jdbc;

    @Override
    public List<RatingMPA> getAllMpa() {
        log.info("Получен запрос на получение списка всех рейтингов MPA");
        final String GET_ALL_MPA = "SELECT * FROM rating_mpa;";
        List<RatingMPA> mpaList = jdbc.query(GET_ALL_MPA, rs -> {
            Map<Long, RatingMPA> mpaMap = new LinkedHashMap<>();
            while (rs.next()) {
                Long mpaId = rs.getLong("rating_mpa_id");
                mpaMap.putIfAbsent(mpaId, new RatingMPA(mpaId, rs.getString("name")));
            }
            return new ArrayList<>(mpaMap.values());
        });
        log.info("Список рейтингов MPA успешно получен");
        return mpaList;
    }

    @Override
    public RatingMPA getMpaById(Long id) throws ValidationException {
        log.info("Получен запрос на получение рейтинга MPA с ID {}", id);

        final String FIND_MPA_BY_ID = "SELECT * FROM rating_mpa WHERE rating_mpa_id = ?;";
        try {
            RatingMPA ratingMPA = jdbc.queryForObject(FIND_MPA_BY_ID, (rs, rowNum) ->
                    new RatingMPA(rs.getLong("rating_mpa_id"), rs.getString("name")), id);

            log.info("Рейтинг MPA с ID {} найден: {}", id, ratingMPA);
            return ratingMPA;
        } catch (EmptyResultDataAccessException e) {
            log.warn("Рейтинг MPA с ID {} не найден", id);
            throw new ValidationException("Рейтинг MPA с ID " + id + " не найден");
        }
    }

}
