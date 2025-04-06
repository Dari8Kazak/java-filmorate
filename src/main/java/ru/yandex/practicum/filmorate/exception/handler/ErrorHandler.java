package ru.yandex.practicum.filmorate.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(final NotFoundException ex) {
        log.error("Not_found {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex, "NOT_FOUND");
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(final ValidationException ex) {
        log.error("Validation_error {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex, "VALIDATION_ERROR");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleSpringValidation(final MethodArgumentNotValidException ex) {
        log.error("Spring_validation_error {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex, "SPRING_VALIDATION_ERROR");
    }

    private void logError(String logPrefix, Exception ex) {
        log.error("{} {}", logPrefix, ex.getMessage());
    }

    private static ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, Exception ex, String errorCode) {
        Map<String, String> details = new HashMap<>();
        if (ex instanceof ValidationException validationEx) {
            details.put("entity", validationEx.getEntityType());
            details.put("field", validationEx.getField());
        } else {
            details.put("errorMessage", ex.getMessage());
        }
        ErrorResponse errorResponse = new ErrorResponse(
                status.value(),
                errorCode,
                "Ошибка_валидации_полей",
                details);
        return ResponseEntity.status(status).body(errorResponse);
    }
}