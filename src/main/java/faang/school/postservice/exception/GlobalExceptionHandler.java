package faang.school.postservice.exception;


import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {

        List<ApiError.FieldErrorDetail> validationErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> new ApiError.FieldErrorDetail(fieldError.getField(), fieldError.getDefaultMessage()))
                .collect(Collectors.toList());

        return new ApiError("Validation failed", validationErrors);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleEntityNotFoundException(EntityNotFoundException ex) {

        log.warn("Entity not found: {}", ex.getMessage(), ex);
        return new ApiError(ex.getMessage(), Collections.emptyList());
    }

    @ExceptionHandler({DataValidationException.class, IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleDataValidationAndIllegalArgumentExceptions(Exception ex) {

        log.warn("Validation or argument error: {}", ex.getMessage(), ex);
        return new ApiError(ex.getMessage(), Collections.emptyList());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleConstraintViolationException(ConstraintViolationException ex) {

        log.warn("Constraint violation exception: {}", ex.getMessage(), ex);

        List<ApiError.FieldErrorDetail> errors = ex.getConstraintViolations().stream()
                .map(violation -> {
                    log.warn("Constraint violation: {} - {}", violation.getPropertyPath(), violation.getMessage());
                    return new ApiError.FieldErrorDetail(
                            violation.getPropertyPath().toString(),
                            violation.getMessage()
                    );
                })
                .collect(Collectors.toList());

        return new ApiError("Constraint violation", errors);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleGenericException(Exception ex) {
        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        return new ApiError("Internal server error. Please try again later.", Collections.emptyList());
    }
}
