package faang.school.postservice.exception;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.event.Level;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
//@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String ERROR_FIELD_INFO = "Info";
    private static final String ERROR_FIELD_EXCEPTION = "Exception";
    private static final String ERROR_FIELD_MESSAGE = "Exception message";

    private static final String INFO_VALIDATION_EXCEPTION = "Data Validation exception occurred";


    @ExceptionHandler(DataValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleDataValidationException(DataValidationException e) {
        Map<String, String> errors = getErrorsMapWithExceptionTitle(e, INFO_VALIDATION_EXCEPTION);

        logging(Level.WARN, errors);
        return errors;
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleEntityNotFoundException(EntityNotFoundException e) {
        Map<String, String> errors = getErrorsMapWithExceptionTitle(e, "Entity not Found exception occurred");

        logging(Level.WARN, errors);
        return errors;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleIllegalArgumentException(IllegalArgumentException e) {
        Map<String, String> errors = getErrorsMapWithExceptionTitle(e, "Illegal argument exception occurred");

        logging(Level.WARN, errors);
        return errors;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleArgumentNotValidException(MethodArgumentNotValidException e) {
        Map<String, String> errors = getErrorsMapWithExceptionTitle(e, INFO_VALIDATION_EXCEPTION);
        Level loggingLevel = Level.WARN;

        if (isLoggable(loggingLevel)) {
            e.getBindingResult()
                    .getFieldErrors()
                    .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

            logging(loggingLevel, errors);
        }
        return errors;
    }

    @ExceptionHandler(jakarta.validation.ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleConstraintViolationException(jakarta.validation.ConstraintViolationException e) {
        Map<String, String> errors = getErrorsMapWithExceptionTitle(e, INFO_VALIDATION_EXCEPTION);
        Level loggingLevel = Level.WARN;

        if (isLoggable(loggingLevel)) {
            e.getConstraintViolations()
                    .forEach(violation -> {
                        errors.put(violation.getRootBeanClass().getSimpleName(), violation.getRootBean().toString());
                        errors.put(violation.getPropertyPath().toString(), violation.getMessage());
                    });

            logging(loggingLevel, errors);
        }
        return errors;
    }

    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleConstraintViolationException(org.springframework.dao.DataIntegrityViolationException e) {
        Map<String, String> errors = getErrorsMapWithExceptionTitle(e, INFO_VALIDATION_EXCEPTION);
        Level loggingLevel = Level.WARN;

        if (isLoggable(loggingLevel)) {
            Throwable rootCause = e.getRootCause();
            if (null != rootCause) {
                if (rootCause instanceof org.hibernate.exception.ConstraintViolationException hibernateException) {
                    subHandleHibernateConstraintViolationException(hibernateException, errors);
                } else if (rootCause instanceof java.sql.SQLException sqlException) {
                    subHandleSQLException(sqlException, errors);
                } else {
                    errors.put("DataIntegrityViolationException: Root cause", rootCause.toString());
                    subHandleException(e, errors);
                }
            }

            logging(loggingLevel, errors);
        }
        return errors;
    }

    @ExceptionHandler(org.hibernate.JDBCException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleJDBCException(org.hibernate.JDBCException e) {
        Map<String, String> errors = getErrorsMapWithExceptionTitle(e, "JDBC exception occurred");
        Level loggingLevel = Level.WARN;

        if (isLoggable(loggingLevel)) {
            java.sql.SQLException sqlException = e.getSQLException();
            subHandleSQLException(sqlException, errors);

            logging(loggingLevel, errors);
        }
        return errors;
    }

    @ExceptionHandler(SQLException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleSQLException(SQLException e) {
        Map<String, String> errors = getErrorsMapWithExceptionTitle(e, "Database error occurred");
        Level loggingLevel = Level.ERROR;

        if (isLoggable(loggingLevel)) {
            subHandleSQLException(e, errors);

            logging(loggingLevel, errors);
        }
        return errors;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleOtherException(Exception e) {
        Map<String, String> errors = getErrorsMapWithExceptionTitle(e, "Exception occurred");
        Level loggingLevel = Level.ERROR;

        if (isLoggable(loggingLevel)) {
            subHandleException(e, errors);

            logging(loggingLevel, errors);
        }
        return errors;
    }



    private void subHandleSQLException(SQLException sqlException, Map<String, String> errors) {
        String title = "SQLException: ";
        errors.put(title + "error", sqlException.getClass().getSimpleName());
        errors.put(title + "message", sqlException.getMessage());
        errors.put(title + "code", String.valueOf(sqlException.getErrorCode()));
        errors.put(title + "state", sqlException.getSQLState());
    }

    private void subHandleHibernateConstraintViolationException(org.hibernate.exception.ConstraintViolationException  hibernateEcxeption, Map<String, String> errors) {
        String title = "Hibernate ConstraintViolationException: ";
        errors.put(title + "error", hibernateEcxeption.getClass().getSimpleName());
        errors.put(title + "message", hibernateEcxeption.getSQLException().getMessage());
        errors.put(title + "Violation of validation", hibernateEcxeption.getConstraintName());
    }

    private void subHandleException(Exception exception, Map<String, String> errors) {
        String additionalMessage = exception.getMessage();
        if (!errors.get(ERROR_FIELD_MESSAGE).equals(additionalMessage)) {
            errors.put("Additional message", additionalMessage);
        }

        if (null != exception.getCause()) {
            errors.put("Cause", exception.getCause().toString());
        }
    }



    private Map<String, String> getErrorsMapWithExceptionTitle(Exception e, String info) {
        Map<String, String> errors = new LinkedHashMap<>();

        errors.put(ERROR_FIELD_INFO, info);
        errors.put(ERROR_FIELD_EXCEPTION, e.getClass().getSimpleName());
        errors.put(ERROR_FIELD_MESSAGE, e.getMessage());

        return errors;
    }


    private void logging(org.slf4j.event.Level level, Map<String, String> errors) {
        errors.forEach((key, value) -> log.makeLoggingEventBuilder(level).log("{}: {}", key, value));
    }

    private boolean isLoggable(org.slf4j.event.Level level) {
        return switch (level) {
            case TRACE -> log.isTraceEnabled();
            case DEBUG -> log.isDebugEnabled();
            case INFO -> log.isInfoEnabled();
            case WARN -> log.isWarnEnabled();
            case ERROR -> log.isErrorEnabled();
        };
    }
}