package faang.school.postservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });

        log.error("Not valid data ", ex);
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(RequiredOwnerException.class)
    public ResponseEntity<Object> handleRequireOwner(RequiredOwnerException ex) {
        log.error("Error. Required owner ", ex);
        return buildResponseEntity(HttpStatus.BAD_REQUEST, ex.getMessage(), null);
    }

    @ExceptionHandler(SinglePostAuthorException.class)
    public ResponseEntity<Object> handleSingleAuthor(SinglePostAuthorException ex) {
        log.error("Error. Must be single author ", ex);
        return buildResponseEntity(HttpStatus.BAD_REQUEST, ex.getMessage(), null);
    }

    @ExceptionHandler(DataAlreadyExistException.class)
    public ResponseEntity<Object> handleDataAlreadyExist(DataAlreadyExistException ex) {
        log.error("Error. Data already exist ", ex);
        return buildResponseEntity(HttpStatus.BAD_REQUEST, ex.getMessage(), null);
    }

    @ExceptionHandler(DataUpdateException.class)
    public ResponseEntity<Object> handleDataUpdated(DataUpdateException ex) {
        log.error("Error. When updating data ", ex);
        return buildResponseEntity(HttpStatus.BAD_REQUEST, ex.getMessage(), null);
    }

    @ExceptionHandler(DataAlreadyDeletedException.class)
    public ResponseEntity<Object> handleAlreadyDeleted(DataAlreadyDeletedException ex) {
        log.error("Error. Already deleted ", ex);
        return buildResponseEntity(HttpStatus.BAD_REQUEST, ex.getMessage(), null);
    }

    @ExceptionHandler(UnpublishedPostException.class)
    public ResponseEntity<Object> handleUnpublishedPost(UnpublishedPostException ex) {
        log.error("Error. Unpublished post ", ex);
        return buildResponseEntity(HttpStatus.BAD_REQUEST, ex.getMessage(), null);
    }

    private ResponseEntity<Object> buildResponseEntity(HttpStatus status, String message, Map<String, String> errors) {
        ApiError apiError = new ApiError(status, message, errors, LocalDateTime.now());
        return ResponseEntity.status(status).body(apiError);
    }
}
