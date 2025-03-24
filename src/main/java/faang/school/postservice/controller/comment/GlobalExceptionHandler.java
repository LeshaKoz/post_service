package faang.school.postservice.controller.comment;

import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Глобальный обработчик исключений для всех контроллеров.
 *
 * @author Zhltsk-V
 * @version 1.0
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Обрабатывает исключения типа DataValidationException.
     *
     * @param e Исключение.
     * @return Ответ с сообщением об ошибке и статусом HTTP 400 (Bad Request).
     */
    @ExceptionHandler(DataValidationException.class)
    public ResponseEntity<String> handlerDataValidationException(DataValidationException e) {
        return ResponseEntity.badRequest().body("Ошибка валидации данных: " + e.getMessage());
    }

    /**
     * Обрабатывает исключения типа EntityNotFoundException.
     *
     * @param e Исключение.
     * @return Ответ с сообщением об ошибке и статусом HTTP 404 (Not Found).
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handlerEntityNotFoundException(EntityNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Сущность не найдена: " + e.getMessage());
    }

    /**
     * Обрабатывает исключения типа MethodArgumentNotValidException.
     *
     * @param exception Исключение.
     * @return Ответ с сообщением об ошибках валидации и статусом HTTP 400 (Bad Request).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();

        exception.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    /**
     * Обрабатывает все остальные исключения.
     *
     * @param e Исключение.
     * @return Ответ с сообщением об ошибке и статусом HTTP 500 (Internal Server Error).
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handlerException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Произошла внутренняя ошибка: " + e.getMessage());
    }
}
