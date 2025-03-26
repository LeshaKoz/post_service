package faang.school.postservice.controller;

import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataValidationException.class)
    public ResponseEntity<String> handleDataValidationException(DataValidationException exception) {
        return ResponseEntity.badRequest().body(exception.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleEntityNotFoundException(EntityNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
    }

    /**
     * Обрабатывает NullPointerException, включая ошибки валидации @NotNull.
     * <p>
     * Логирует предупреждение и возвращает ответ со статусом 400 (Bad Request).
     *
     * @param exception исключение типа NullPointerException
     * @return ResponseEntity с HTTP-статусом 400 и сообщением об ошибке,
     * указывающим на отсутствующее обязательное поле
     */
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<String> handleNullPointerException(NullPointerException exception) {
        log.warn("Null pointer: {}", exception.getMessage(), exception);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(String.format("Не заполнено обязательное поле: %s",
                        exception.getMessage()));
    }

    /**
     * Обрабатывает исключения FeignClient при взаимодействии с внешними сервисами.
     * <p>
     * Пробрасывает оригинальный HTTP-статус из исключения и возвращает сообщение
     * об ошибке внешнего сервиса.
     *
     * @param exception исключение типа FeignException
     * @return ResponseEntity с оригинальным HTTP-статусом из исключения
     * и сообщением об ошибке внешнего сервиса
     */
    @ExceptionHandler(FeignException.class)
    public ResponseEntity<String> handleFeignException(FeignException exception) {
        log.error("Feign exception: {}", exception.getMessage(), exception);
        return ResponseEntity.status(exception.status())
                .body(String.format("Ошибка внешнего сервиса: %s",
                        exception.getMessage()));
    }
}
