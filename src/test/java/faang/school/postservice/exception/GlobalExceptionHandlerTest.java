package faang.school.postservice.exception;

import jakarta.persistence.EntityNotFoundException;
import org.hibernate.JDBCException;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private static final String ERROR_FIELD_INFO = "Info";
    private static final String ERROR_FIELD_EXCEPTION = "Exception";
    private static final String ERROR_FIELD_MESSAGE = "Exception message";

    private static final GlobalExceptionHandler globalHandler = new GlobalExceptionHandler();


    private static final DataValidationException dataValidationException = new DataValidationException("DataValidationException");;
    private static final EntityNotFoundException entityNotFoundException = new EntityNotFoundException("EntityNotFoundException");
    private static final IllegalArgumentException illegalArgumentException = new IllegalArgumentException("IllegalArgumentException");
    private static final org.springframework.dao.DataIntegrityViolationException dataIntegrityViolationException = new DataIntegrityViolationException("DataIntegrityViolationException");
    private static final SQLException sqlException = new SQLException("SQLException");
    private final static MethodArgumentNotValidException methodArgumentNotValidException = getMethodArgumentNotValidException();
    private static final Exception exception = new Exception("Exception");
    @Mock
    private static jakarta.validation.ConstraintViolationException jakartaConstraintViolationException;
    @Mock
    private static JDBCException jdbcException;


    @Test
    void dataValidationExceptionHandler() {
        assertErrorsMap(
                globalHandler.handleDataValidationException(dataValidationException));
    }

    @Test
    void entityNotFoundExceptionHandler() {
        assertErrorsMap(
                globalHandler.handleEntityNotFoundException(entityNotFoundException));
    }

    @Test
    void illegalArgumentExceptionHandler() {
        assertErrorsMap(
                globalHandler.handleIllegalArgumentException(illegalArgumentException));
    }

    @Test
    void methodHandleArgumentNotValidException() {
        assertErrorsMap(
                globalHandler.handleArgumentNotValidException(methodArgumentNotValidException));
    }

    @Test
    void jakartaConstraintViolationExceptionHandler() {
        when(jakartaConstraintViolationException.getConstraintViolations())
                .thenReturn(Set.of());

        assertErrorsMap(
                globalHandler.handleConstraintViolationException(jakartaConstraintViolationException));
    }

    @Test
    void dataIntegrityViolationExceptionHandler() {
        assertErrorsMap(
                globalHandler.handleConstraintViolationException(dataIntegrityViolationException));
    }

    @Test
    void jdbcExceptionHandler() {
        when(jdbcException.getSQLException())
                .thenReturn(sqlException);

        assertErrorsMap(
                globalHandler.handleJDBCException(jdbcException));
    }

    @Test
    void sqlExceptionHandler() {
        assertErrorsMap(
                globalHandler.handleSQLException(sqlException));

    }

    @Test
    void otherExceptionHandler() {
        assertErrorsMap(
                globalHandler.handleOtherException(exception));
    }


    private static void assertErrorsMap(Map<String, String> errors) {
        assertNotNull(errors);
        assertTrue(errors.size() >=3);

        for (String field : List.of(ERROR_FIELD_INFO, ERROR_FIELD_EXCEPTION, ERROR_FIELD_MESSAGE)) {
            assertTrue(errors.containsKey(field));
        }
    }

    private static MethodArgumentNotValidException getMethodArgumentNotValidException() {
        Method method = null;
        try {
            method = GlobalExceptionHandlerTest.class.getDeclaredMethod("method", String.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        MethodParameter methodParameter = new MethodParameter(method, 0);

        BindException bindException = new BindException(new BeanPropertyBindingResult(null, "BindObject"));

        return new MethodArgumentNotValidException(methodParameter, bindException.getBindingResult());
    }

    private void method(String str) {}
}
