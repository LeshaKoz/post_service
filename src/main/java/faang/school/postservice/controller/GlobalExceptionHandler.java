package faang.school.postservice.controller;

import faang.school.postservice.exceptions.ErrorResponse;
import faang.school.postservice.exceptions.UserServiceConnectException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(UserServiceConnectException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorResponse handlePostNotFoundException(Exception e, WebRequest webRequest) {
        return buildErrorMessage(e, webRequest);
    }

    private ErrorResponse buildErrorMessage(Exception exception, WebRequest webRequest) {
        String path = webRequest.getDescription(false).replace("uri=", "");
        return ErrorResponse.builder()
                .message(exception.getMessage())
                .path(path)
                .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e, WebRequest request) {
        List<Map<String, String>> fieldErrors = e.getBindingResult().getFieldErrors().stream().map(fieldError -> Map.of(
                "field", fieldError.getField(),
                "message", Objects.requireNonNull(fieldError.getDefaultMessage())
        )).toList();

        log.error("MethodArgumentNotValidException: ", e);
        return ErrorResponse.builder()
                .path(request.getContextPath())
                .details(fieldErrors)
                .message(e.getMessage())
                .build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgumentException(IllegalArgumentException e, WebRequest request) {
        log.error("IllegalArgumentException: ", e);
        return ErrorResponse.builder()
                .path(request.getContextPath())
                .message(e.getMessage())
                .build();
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleEntityNotFoundException(EntityNotFoundException e, WebRequest request) {
        log.error("EntityNotFoundException: ", e);
        return ErrorResponse.builder()
                .path(request.getContextPath())
                .message(e.getMessage())
                .build();
    }

}
