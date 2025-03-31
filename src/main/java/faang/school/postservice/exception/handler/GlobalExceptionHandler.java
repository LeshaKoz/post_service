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
                        "We encountered an issue while checking your post text. Please try again later.")
                .title("Text Validation Service Unavailable")
                .property("service", "LanguageTool")
                .build();
        return new ResponseEntity<>(errorResponse, exception.getStatusCode());
    }

    @ExceptionHandler(InvalidPostAuthorsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPostAuthorsException(InvalidPostAuthorsException exception) {
        ErrorResponse errorResponse = ErrorResponse.builder(exception, HttpStatus.BAD_REQUEST,
                        "A post cannot have both a user and a project as authors. Please specify only one author" +
                                " (either a user or a project).")
                .title("Invalid Post Author")
                .property("service", "PostService")
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PostNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePostNotFoundException(PostNotFoundException exception) {
        ErrorResponse errorResponse = ErrorResponse.builder(exception, HttpStatus.NOT_FOUND,
                        "The post you're looking for doesn't exist or may have been deleted.")
                .title("Post Not Found")
                .property("service", "PostService")
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException exception) {
        ErrorResponse errorResponse = ErrorResponse.builder(exception, HttpStatus.BAD_REQUEST,
                        "Your request contains invalid data. Please check your input and try again.")
                .title("Invalid Request")
                .property("service", "PostService")
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}