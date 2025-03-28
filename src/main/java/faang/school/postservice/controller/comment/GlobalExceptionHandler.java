package faang.school.postservice.controller.comment;

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
 * Глобальный обработчик исключений для всех контроллеров приложения.
 *
 * <p>Поддерживаемые обработчики исключений:</p>
 * <ul>
 *   <li>{@link #handlerDataValidationException(DataValidationException)}
 *   - Обрабатывает ошибки валидации данных (HTTP 400)</li>
 *   <li>{@link #handlerEntityNotFoundException(EntityNotFoundException)}
 *   - Обрабатывает случаи ненайденных сущностей (HTTP 404)</li>
 *   <li>{@link #handleValidationExceptions(MethodArgumentNotValidException)}
 *   - Обрабатывает ошибки валидации Spring Bean (HTTP 400)</li>
 *   <li>{@link #handlerException(Exception)}
 *   - Универсальный обработчик для всех непредвиденных исключений (HTTP 500)</li>
 * </ul>
 *
 * @author Zhltsk-V
 * @version 1.0
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Обрабатывает исключения типа {@link DataValidationException} (ошибки валидации данных).
     *
     * @param exception перехваченное исключение
     * @return ответ с сообщением об ошибке (HTTP 400 Bad Request)
     */
    @ExceptionHandler(DataValidationException.class)
    public ResponseEntity<String> handlerDataValidationException(DataValidationException exception) {
        log.error("Data validation error: {}", exception.getMessage());
        return ResponseEntity.badRequest().body("Data validation error: " + exception.getMessage());
    }

    /**
     * Обрабатывает исключения типа {@link EntityNotFoundException} (ненайденные сущности).
     *
     * @param exception перехваченное исключение
     * @return ответ с сообщением об ошибке (HTTP 404 Not Found)
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handlerEntityNotFoundException(EntityNotFoundException exception) {
        log.error("Entity not found: {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Entity not found: " + exception.getMessage());
    }

    /**
     * Обрабатывает исключения типа {@link MethodArgumentNotValidException} (ошибки валидации параметров методов).
     *
     * @param exception перехваченное исключение
     * @return ответ с картой ошибок валидации (HTTP 400 Bad Request)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();

        exception.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        log.error("Data validation error: {}", errors);
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    /**
     * Универсальный обработчик для всех неперехваченных исключений.
     *
     * @param exception перехваченное исключение
     * @return ответ с сообщением об ошибке (HTTP 500 Internal Server Error)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handlerException(Exception exception) {
        log.error("Internal server error occurred: {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Internal server error occurred: " + exception.getMessage());
    }
}