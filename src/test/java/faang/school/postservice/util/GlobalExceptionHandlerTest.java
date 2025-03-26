package faang.school.postservice.util;

import faang.school.postservice.controller.GlobalExceptionHandler;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
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

    @DisplayName("Обработка DataValidationException: должен возвращать статус BAD_REQUEST и сообщение об ошибке валидации")
    @Test
    public void givenDataValidationExceptionWhenGlobalExceptionHandelThenBadRequest() {
        DataValidationException exception = new DataValidationException("Invalid data");

        response = globalExceptionHandler.handleDataValidationException(exception);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals("Ошибка валидации данных: Invalid data", response.getBody());
    }

    @DisplayName("Обработка EntityNotFoundException: должен возвращать статус NOT_FOUND и сообщение о ненайденной сущности")
    @Test
    public void givenEntityNotFoundExceptionWhenGlobalExceptionHandelThenNotFound() {
        EntityNotFoundException exception = new EntityNotFoundException("Entity not found");

        response = globalExceptionHandler.handleEntityNotFoundException(exception);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Assertions.assertEquals("Сущность не найдена: Entity not found", response.getBody());
    }

    @DisplayName("Обработка общего Exception: должен возвращать статус INTERNAL_SERVER_ERROR и сообщение о внутренней ошибке")
    @Test
    public void givenExceptionWhenGlobalExceptionHandelThenInternalServerError() {
        Exception exception = new Exception("Internal server error");

        response = globalExceptionHandler.handleException(exception);

        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Assertions.assertEquals("Произошла внутренняя ошибка: Internal server error", response.getBody());
    }

    @DisplayName("Обработка MethodArgumentNotValidException: должен возвращать статус BAD_REQUEST и map с ошибками валидации")
    @Test
    public void givenMethodArgumentNotValidExceptionWhenGlobalExceptionHandelThenBadRequest() {
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

    @DisplayName("Обработка NullPointerException: должен возвращать статус BAD_REQUEST и сообщение о незаполненном поле")
    @Test
    void givenNullPointerExceptionWhenHandleNullPointerExceptionThenBadRequest() {
        NullPointerException exception = new NullPointerException("");

        response = globalExceptionHandler.handleNullPointerException(exception);

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertTrue(response.getBody().contains("Не заполнено обязательное поле"));
    }
}
