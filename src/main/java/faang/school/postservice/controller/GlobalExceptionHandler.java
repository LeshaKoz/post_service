package faang.school.postservice.controller;

import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

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

/**
 * Обрабатывает исключение {@link MethodArgumentNotValidException}, которое возникает при валидации данных,
 * переданных в метод контроллера. Этот метод собирает все ошибки валидации и возвращает их в виде
 * JSON-объекта, где ключ — это имя поля, а значение — сообщение об ошибке.
 *
 * @param exception Исключение {@link MethodArgumentNotValidException}, содержащее информацию об ошибках валидации.
 * @return Объект {@link ResponseEntity}, содержащий:
 *         <ul>
 *             <li>Тело ответа в виде {@link Map}, где ключ — это имя поля, а значение — сообщение об ошибке.</li>
 *             <li>HTTP-статус {@link HttpStatus#BAD_REQUEST} (400), указывающий на некорректный запрос
 */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();

        exception.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}