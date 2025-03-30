package faang.school.postservice.exception.handler;

import faang.school.postservice.exception.InvalidPostAuthorsException;
import faang.school.postservice.exception.LanguageToolException;
import faang.school.postservice.exception.PostNotFoundException;
import org.springframework.http.HttpStatus;
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

    @ExceptionHandler(InvalidPostAuthorsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPostAuthorsException(InvalidPostAuthorsException exception) {
        ErrorResponse errorResponse = ErrorResponse.builder(exception, HttpStatus.BAD_REQUEST,
                        exception.getMessage())
                .title("Invalid post authors error")
                .property("service", "PostService")
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PostNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePostNotFoundException(PostNotFoundException exception) {
        ErrorResponse errorResponse = ErrorResponse.builder(exception, HttpStatus.BAD_REQUEST,
                        exception.getMessage())
                .title("Post not found")
                .property("service", "PostService")
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException exception) {
        ErrorResponse errorResponse = ErrorResponse.builder(exception, HttpStatus.BAD_REQUEST,
                        exception.getMessage())
                .title("Invalid request data")
                .property("service", "PostService")
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
