DROP TABLE IF EXISTS rating_mpa, genres, users, status, films, friendship, likes, film_genres CASCADE;

CREATE TABLE IF NOT EXISTS rating_mpa (
                                          rating_mpa_id INTEGER GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
                                          name VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS genres (
                                      genre_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
                                      name VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS users (
                                     user_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
                                     login VARCHAR NOT NULL,
                                     name VARCHAR NOT NULL,
                                     email VARCHAR NOT NULL,
                                     birthday DATE NOT NULL,
                                     CONSTRAINT unique_login_email UNIQUE (login, email)
    );

CREATE TABLE IF NOT EXISTS status (
                                      status_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
                                      name VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS films (
                                     film_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
                                     name VARCHAR NOT NULL,
                                     description VARCHAR,
                                     release_date DATE NOT NULL,
                                     duration INTEGER,
                                     rating_mpa_id INTEGER,
                                     CONSTRAINT fk_rating_mpa FOREIGN KEY (rating_mpa_id) REFERENCES rating_mpa(rating_mpa_id)
    );

CREATE TABLE IF NOT EXISTS friendship (
                                          user_id INTEGER NOT NULL,
                                          friend_id INTEGER NOT NULL,
                                          status_id INTEGER,
                                          PRIMARY KEY (user_id, friend_id),
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(user_id),
    CONSTRAINT fk_friend FOREIGN KEY (friend_id) REFERENCES users(user_id),
    CONSTRAINT fk_status FOREIGN KEY (status_id) REFERENCES status(status_id)
    );

CREATE TABLE IF NOT EXISTS likes (
                                     film_id INTEGER NOT NULL,
                                     user_id INTEGER NOT NULL,
                                     PRIMARY KEY (film_id, user_id),
    CONSTRAINT fk_likes_film FOREIGN KEY (film_id) REFERENCES films(film_id),
    CONSTRAINT fk_likes_user FOREIGN KEY (user_id) REFERENCES users(user_id)
    );

CREATE TABLE IF NOT EXISTS film_genres (
                                           film_id INTEGER NOT NULL,
                                           genre_id INTEGER NOT NULL,
                                           PRIMARY KEY (film_id, genre_id),
    CONSTRAINT fk_film_genre_film FOREIGN KEY (film_id) REFERENCES films(film_id),
    CONSTRAINT fk_film_genre_genre FOREIGN KEY (genre_id) REFERENCES genres(genre_id)
    );