package faang.school.postservice.advice;

import faang.school.postservice.exception.DataValidationException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleAnnotatedValidationExceptions(MethodArgumentNotValidException e) {
        var response = e.getBindingResult().getAllErrors().stream()
                .collect(Collectors.toMap(
                                error -> ((FieldError) error).getField(),
                                error -> Objects.requireNonNullElse(error.getDefaultMessage(), "")
                        )
                );
        log.error("Method argument validation occurred, errors {}, \nexception: {}",
                response.entrySet().stream()
                        .map(entry -> entry.getKey() + " " + entry.getValue())
                        .reduce("", (current, field) -> current + "\n\t" + field),
                e.getStackTrace());
        return response;
    }

    @ExceptionHandler(DataValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleValidationExceptions(DataValidationException e) {
        log.error("Validation exception occurred {}\n{}", e.getMessage(), e.getStackTrace());
        return e.getMessage();
    }

    @ExceptionHandler({NoSuchElementException.class, EntityNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFoundExceptions(RuntimeException e) {
        log.error("Not found in database exception occurred, {}\n{}", e.getMessage(), e.getStackTrace());
        return e.getMessage();
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleRuntimeExceptions(RuntimeException e) {
        log.error("Some exception occurred, {}\n{}", e.getMessage(), e.getStackTrace());
        return e.getMessage();
    }
}
