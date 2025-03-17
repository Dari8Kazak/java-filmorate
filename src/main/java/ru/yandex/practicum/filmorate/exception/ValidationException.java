package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;

@Getter
public class ValidationException extends RuntimeException {
    private final String entityType;
    private final String field;

    public ValidationException(String entityType, String field, String message) {
        super(message);
        this.entityType = entityType;
        this.field = field;
    }

}