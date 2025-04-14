package ru.yandex.practicum.filmorate.storage;

import com.zaxxer.hikari.HikariDataSource;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.*;
import java.sql.Date;
import java.util.*;

@Slf4j
@AllArgsConstructor
@Repository
public class UserDbStorage implements UserStorage {
    private JdbcTemplate jdbc;
    private final Map<Long, User> users = new HashMap<>();
    private HikariDataSource dataSource;

    @Override
    public List<User> findAllUsers() {
        final String GET_ALL_USERS = """
                SELECT u.*,\s
                       f.friend_id
                FROM users AS u
                LEFT JOIN friendship AS f\s
                  ON u.user_id = f.user_id AND f.status_id = 1;""";
        return getUsersList(GET_ALL_USERS);
    }

    @Override
    public List<User> getFriendsList(Long userId) {
        containsUserById(userId);
        final String GET_ACCEPTED_FRIENDS = """
                SELECT\s
                    u.user_id AS user_id,
                    u.login AS login,
                    u.name AS name,
                    u.email AS email,
                    u.birthday AS birthday,
                    fof.user_id AS friend_id
                FROM friendship AS f
                JOIN users AS u ON f.friend_id = u.user_id
                LEFT JOIN friendship AS f2 ON u.user_id = f2.user_id
                LEFT JOIN users AS fof ON f2.friend_id = fof.user_id
                WHERE f.user_id = ?;""";
        return getUsersList(GET_ACCEPTED_FRIENDS, userId);
    }

    @Override
    public List<User> getCommonFriendsList(Long id, Long otherId) {

        containsUserById(id);
        containsUserById(otherId);
        final String GET_COMMON_FRIENDS = """
                SELECT u.user_id AS user_id,
                       u.login AS login,
                       u.name AS name,
                       u.email AS email,
                       u.birthday AS birthday,
                       f1.friend_id AS friend_id
                FROM users AS u
                JOIN friendship AS f1 ON u.user_id = f1.friend_id
                JOIN friendship AS f2 ON u.user_id = f2.friend_id
                WHERE f1.user_id = ? AND f2.user_id = ?;""";
        return getUsersList(GET_COMMON_FRIENDS, id, otherId);
    }

    @Override
    public User createUser(User user) {
        final String CREATE_USER = "INSERT INTO users (login, name, email, birthday) " +
                "VALUES (?, ?, ?, ?);";
        final GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(CREATE_USER, Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, user.getLogin());
            ps.setString(2, user.getName());
            ps.setString(3, user.getEmail());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);

        final Integer generatedId = (Integer) Objects.requireNonNull(keyHolder.getKeys()).get("user_id");
        if (generatedId != null) {
            user.setId(Long.valueOf(generatedId));
        } else {
            throw new RuntimeException("Не удалось получить сгенерированный ID для пользователя");
        }
        return user;
    }

    @Override
    public User updateUser(User newUser) {
        final String UPDATE_USER = "UPDATE users SET " +
                "login = ?, name = ?, email = ?, birthday = ? " +
                "WHERE user_id = ?;";
        Long newUserId = newUser.getId();

        int rowsAffected = jdbc.update(UPDATE_USER,
                newUser.getLogin(),
                newUser.getName(),
                newUser.getEmail(),
                newUser.getBirthday(),
                newUserId
        );

        if (rowsAffected == 0) {
            log.warn("Пользователь с ID {} не найден для обновления.", newUserId);
            return null;
        }

        log.info("Данные пользователя с ID {} успешно обновлены.", newUserId);
        return newUser;
    }

    @Override
    public User addFriend(Long userId, Long friendId) {
        containsUserById(userId);
        containsUserById(friendId);

        if (userId.equals(friendId)) {
            log.warn("Попытка добавить самого себя в друзья: userId = {}", userId);
            return null;
        }

        if (pairExists(userId, friendId)) {
            log.info("Прямая дружба уже существует: пользователь {} уже в друзьях у {}", friendId, userId);
            return null;
        }

        if (pairExists(friendId, userId)) {
            final String INSERT_DIRECT_PAIR = "MERGE INTO friendship (user_id, friend_id, status_id) KEY(user_id, friend_id) VALUES (?, ?, ?)";
            jdbc.update(INSERT_DIRECT_PAIR, userId, friendId, 1);
            log.info("Дружба подтверждена: пользователи {} и {} теперь друзья", userId, friendId);
        } else {
            final String INSERT_FRIENDSHIP = "INSERT INTO friendship (user_id, friend_id, status_id) VALUES (?, ?, 2)";
            jdbc.update(INSERT_FRIENDSHIP, userId, friendId);
            log.info("Запрос в друзья: пользователь {} отправил запрос пользователю {} со статусом 2 (Pending)", userId, friendId);
        }
        return getUserById(userId); // Возвращаем пользователя
    }

    @SneakyThrows
    @Override
    public boolean removeFriend(Long userId, Long friendId) {
        log.info("Запрос на удаление друга: userId = {}, friendId = {}", userId, friendId);
        containsUserById(userId);
        containsUserById(friendId);

        if (!pairExists(userId, friendId)) {
            log.info("Пользователь {} не имеет в друзьях пользователя {}", userId, friendId);
            return false;
        }

        jdbc.update("DELETE FROM friendship WHERE user_id = ? AND friend_id = ?", userId, friendId);
        log.info("Удалена запись дружбы: {} -> {}", userId, friendId);

        if (pairExists(friendId, userId)) {
            jdbc.update("UPDATE friendship SET status_id = 2 WHERE user_id = ? AND friend_id = ?", friendId, userId);
            log.info("Обновлена запись дружбы: {} теперь имеет статус 2 (Pending)", friendId);
        } else {
            log.info("Односторонняя дружба удалена: пользователь {} больше не в друзьях у пользователя {}", userId, friendId);
        }
        return true;
    }

    @Override
    public void removeUser(Long userId) {
        if (!users.containsKey(userId)) {
            log.info("userId: {} -> не найден", userId);
        }
        users.remove(userId);
    }

    public User getUserById(Long id) {
        log.info("Поиск пользователя с id: {}", id);

        final String GET_USER_BY_ID = "SELECT u.user_id AS user_id,\n" +
                "       u.login AS login,\n" +
                "       u.name AS name,\n" +
                "       u.email AS email,\n" +
                "       u.birthday AS birthday,\n" +
                "       f.friend_id AS friend_id" +
                " FROM users AS u\n" +
                " LEFT JOIN friendship AS f \n" +
                " ON u.user_id = f.user_id AND f.status_id = 1\n" +
                " WHERE u.user_id = ?;";

        List<User> users = jdbc.query(GET_USER_BY_ID, (rs) -> {
            Map<Long, User> userMap = new HashMap<>();

            while (rs.next()) {
                long userId = rs.getLong("user_id");
                User user = userMap.computeIfAbsent(userId, idKey -> {
                    User newUser = new User();
                    try {
                        newUser.setId(idKey);
                        newUser.setLogin(rs.getString("login"));
                        newUser.setName(rs.getString("name"));
                        newUser.setEmail(rs.getString("email"));
                        newUser.setBirthday(rs.getDate("birthday").toLocalDate());
                        newUser.setFriends(new LinkedHashSet<>());
                    } catch (SQLException e) {
                        throw new RuntimeException("Ошибка чтения данных пользователя", e);
                    }
                    return newUser;
                });

                user.getFriends().add(rs.getLong("friend_id"));
            }

            return new ArrayList<>(userMap.values());
        }, id);

        assert users != null;
        return null;
    }

    private List<User> getUsersList(String sqlQuery, Object... params) {
        log.info("Начало подготовки списка пользователей");

        try (Connection connection = dataSource.getConnection();
             PreparedStatement pst = connection.prepareStatement(sqlQuery)) {

            for (int i = 0; i < params.length; i++) {
                pst.setObject(i + 1, params[i]);
            }

            try (ResultSet rs = pst.executeQuery()) {
                Map<Long, User> users = new LinkedHashMap<>();

                while (rs.next()) {
                    Long userId = rs.getLong("user_id");
                    User user = users.computeIfAbsent(userId, id -> {
                        User newUser = new User();
                        newUser.setId(id);
                        try {
                            newUser.setLogin(rs.getString("login"));
                            newUser.setName(rs.getString("name"));
                            newUser.setEmail(rs.getString("email"));
                            newUser.setBirthday(rs.getDate("birthday").toLocalDate());
                        } catch (SQLException e) {
                            throw new RuntimeException("Ошибка при получении данных пользователя", e);
                        }
                        newUser.setFriends(new LinkedHashSet<>());
                        return newUser;
                    });

                    Optional.ofNullable(rs.getObject("friend_id"))
                            .map(fid -> {
                                try {
                                    return rs.getInt("friend_id");
                                } catch (SQLException e) {
                                    throw new RuntimeException("Ошибка при получении ID друга", e);
                                }
                            })
                            .ifPresent(friendId -> user.getFriends().add(Long.valueOf(friendId)));
                }

                log.info("Список подготовлен");
                return new ArrayList<>(users.values());
            }
        } catch (SQLException e) {
            log.error("Ошибка при извлечении данных", e);
            throw new RuntimeException("Ошибка при извлечении данных", e);
        }
    }

    @Override
    public boolean containsUserById(Long id) throws ValidationException {
        log.info("Проверка существования пользователя с id: {}", id);
        final String CHECK_USER_EXISTS_BY_ID = "SELECT COUNT(*) FROM users WHERE user_id = ?";
        Long count = jdbc.queryForObject(CHECK_USER_EXISTS_BY_ID, Long.class, id);
        boolean exists = count != null && count > 0;

        if (!exists) {
            log.info("Пользователь с ID {} не найден в базе данных.", id);
            throw new ValidationException("Пользователь c ID " + id + " не найден"); // Кидаем специальное исключение
        }

        log.info("Пользователь с ID {} существует в базе данных.", id);
        return true;
    }

    private boolean pairExists(Long userId, Long friendId) {
        log.info("Начало поиска пары в БД");
        final String CHECK_PAIR =
                "SELECT COUNT(*) FROM friendship WHERE user_id = ? AND friend_id = ?";
        Long count = jdbc.queryForObject(CHECK_PAIR, Long.class, userId, friendId);
        return count != null && count > 0;
    }
}