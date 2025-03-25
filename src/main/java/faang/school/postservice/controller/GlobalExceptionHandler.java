package faang.school.postservice.controller;

import faang.school.postservice.exception.DataValidationException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataValidationException.class)
    public ResponseEntity<String> handleDataValidationException(DataValidationException ex) {
        log.error("Обработано исключение валидации: " + ex.getMessage(), ex);
        return BadRequest(ex);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleEntityNotFoundException(EntityNotFoundException ex) {
        log.error("Обработано исключение отсутствия сущности: " + ex.getMessage(), ex);
        return NotFound(ex);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException ex) {
        log.error("Обработано исключение нарушения ограничений: " + ex.getMessage(), ex);
        return BadRequest(ex);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        log.error("Обработано исключение валидации аргументов метода: " + ex.getMessage(), ex);
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        log.error("Обработано исключение в ходе работы программы: " + ex.getMessage(), ex);
        return InternalServerError(ex);
    }

    private ResponseEntity<String> InternalServerError(Exception ex) {
        return getResponse(ex, 500);
    }

    private ResponseEntity<String> BadRequest(Exception ex) {
        return getResponse(ex, 400);
    }

    private ResponseEntity<String> NotFound(Exception ex) {
        return getResponse(ex, 404);
    }

    private ResponseEntity<String> getResponse(Exception ex, int code) {
        return ResponseEntity
                .status(HttpStatus.valueOf(code))
                .body(ex.getMessage());
    }
}
