package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.RatingMPA;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InMemoryMpaService implements MpaService {

    private final MpaStorage mpaStorage;

    public List<RatingMPA> getAllMpa() {
        return mpaStorage.getAllMpa();
    }

    @SneakyThrows
    public RatingMPA getMpaById(Long id) {
        RatingMPA ratingMPA = mpaStorage.getMpaById(id);
        if (ratingMPA == null) {
            throw new Exception("Рейтинг MPA с ID " + id + " отсутствует");
        }
        return ratingMPA;
    }
}