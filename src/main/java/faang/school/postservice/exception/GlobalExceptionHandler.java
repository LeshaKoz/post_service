package faang.school.postservice.exception;

import faang.school.postservice.dto.post.ResponseError;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Map<Class<? extends Exception>, HttpStatus> EXCEPTION_TO_STATUS = new HashMap<>();

    static {
        EXCEPTION_TO_STATUS.put(EntityNotFoundException.class, HttpStatus.NOT_FOUND);
        EXCEPTION_TO_STATUS.put(UserNotFoundException.class, HttpStatus.NOT_FOUND);
        EXCEPTION_TO_STATUS.put(ProjectNotFoundException.class, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({EntityNotFoundException.class, UserNotFoundException.class, ProjectNotFoundException.class})
    public ResponseError handleException(Exception e, HttpServletRequest request) {
        EXCEPTION_TO_STATUS.getOrDefault(e.getClass(), HttpStatus.INTERNAL_SERVER_ERROR);
        log.error("Exception occurred while handling request at {}: {}", request.getRequestURI(), e.getMessage(), e);

        return new ResponseError(
                "Not found: " + e.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now()
        );
    }
}