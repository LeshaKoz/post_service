package faang.school.postservice.util;

import faang.school.postservice.controller.GlobalExceptionHandler;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Collections;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
public class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    private ResponseEntity<String> response;

    @Test
    public void givenDataValidationExceptionThrowGlobalExceptionHandelThrowBadRequest() {
        DataValidationException exception = new DataValidationException("Invalid data");

        response = globalExceptionHandler.handleDataValidationException(exception);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals("Ошибка валидации данных: Invalid data", response.getBody());
    }

    @Test
    public void givenEntityNotFoundExceptionThrowGlobalExceptionHandelThrowNotFound() {
        EntityNotFoundException exception = new EntityNotFoundException("Entity not found");

        response = globalExceptionHandler.handleEntityNotFoundException(exception);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Assertions.assertEquals("Сущность не найдена: Entity not found", response.getBody());
    }

    @Test
    public void givenExceptionThrowGlobalExceptionHandelThrowInternalServerError() {
        Exception exception = new Exception("Internal server error");

        response = globalExceptionHandler.handleException(exception);

        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Assertions.assertEquals("Произошла внутренняя ошибка: Internal server error", response.getBody());
    }

    @Test
    public void givenMethodArgumentNotValidExceptionThrowGlobalExceptionHandelThrowBadRequest() {
        MethodArgumentNotValidException exception = Mockito.mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = Mockito.mock(BindingResult.class);

        FieldError fieldError = new FieldError("objectName", "fieldName", "defaultMessage");
        Mockito.when(exception.getBindingResult())
                .thenReturn(bindingResult);
        Mockito.when(bindingResult.getFieldErrors())
                .thenReturn(Collections.singletonList(fieldError));

        ResponseEntity<Map<String, String>> response = globalExceptionHandler.handleValidationExceptions(exception);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals(Collections.singletonMap("fieldName", "defaultMessage"), response.getBody());
    }
}
