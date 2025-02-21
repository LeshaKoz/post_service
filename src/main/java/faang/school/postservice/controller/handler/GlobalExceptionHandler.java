package faang.school.postservice.controller.handler;

import faang.school.postservice.dto.error.ErrorModel;
import faang.school.postservice.exception.KafkaProduceException;
import faang.school.postservice.exception.album.AlbumAccessDeniedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @Value("${service.name}")
    private String serviceName;

    @ExceptionHandler(AlbumAccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorModel handleAccessDeniedException(AlbumAccessDeniedException ex) {
        log.error("Album access denied exception", ex);
        return createError(ex.getMessage(), HttpStatus.FORBIDDEN.value());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorModel handleArgumentException(IllegalArgumentException ex) {
        log.error("Illegal argument exception", ex);
        return createError(ex.getMessage(), HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(KafkaProduceException.class)
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    public ErrorModel handleKafkaProduceException(KafkaProduceException ex) {
        log.error("Kafka produce exception", ex);
        return createError(ex.getMessage(), HttpStatus.BAD_GATEWAY.value());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorModel handleException(Exception ex) {
        log.error("Internal server error exception", ex);
        return createError(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    private ErrorModel createError(String message, int statusCode) {
        return new ErrorModel(message, statusCode, serviceName);
    }
}
