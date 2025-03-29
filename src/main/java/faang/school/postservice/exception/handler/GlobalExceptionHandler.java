package faang.school.postservice.exception.handler;

import faang.school.postservice.exception.LanguageToolException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(LanguageToolException.class)
    public ResponseEntity<ErrorResponse> handleLanguageToolException(LanguageToolException exception) {
        ErrorResponse errorResponse = ErrorResponse.builder(exception, exception.getStatusCode(),
                        exception.getMessage())
                .title("LanguageTool service error")
                .property("service", "LanguageTool")
                .build();
        return new ResponseEntity<>(errorResponse, exception.getStatusCode());
    }
}
