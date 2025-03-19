package faang.school.postservice.controller;

import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Глобальный обработчик исключений для контроллеров.
 * Этот класс перехватывает исключения, возникающие в контроллерах, и возвращает соответствующие HTTP-ответы.
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Обрабатывает исключения типа {@link DataValidationException}.
     * Возвращает HTTP-ответ со статусом 400 (Bad Request) и сообщением об ошибке.
     *
     * @param dataValidationException исключение, которое было выброшено
     * @return ResponseEntity с сообщением об ошибке и статусом 400
     */
    @ExceptionHandler(DataValidationException.class)
    public ResponseEntity<String> handleDataValidationException(DataValidationException dataValidationException) {
        log.warn("Data validation error: {}", dataValidationException.getMessage(), dataValidationException);
        return ResponseEntity.badRequest()
                .body("Ошибка валидации данных: " + dataValidationException.getMessage());
    }

    /**
     * Обрабатывает исключения типа {@link EntityNotFoundException}.
     * Возвращает HTTP-ответ со статусом 404 (Not Found) и сообщением об ошибке.
     *
     * @param entityNotFoundException исключение, которое было выброшено
     * @return ResponseEntity с сообщением об ошибке и статусом 404
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleEntityNotFoundException(EntityNotFoundException entityNotFoundException) {
        log.error("Entity not found: {}", entityNotFoundException.getMessage(), entityNotFoundException);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Сущность не найдена: " + entityNotFoundException.getMessage());
    }

    /**
     * Обрабатывает все остальные исключения, которые не были перехвачены другими обработчиками.
     * Возвращает HTTP-ответ со статусом 500 (Internal Server Error) и сообщением об ошибке.
     *
     * @param exception исключение, которое было выброшено
     * @return ResponseEntity с сообщением об ошибке и статусом 500
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception exception) {
        log.error("Internal server error: {}", exception.getMessage(), exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Произошла внутренняя ошибка: " + exception.getMessage());
    }
}
