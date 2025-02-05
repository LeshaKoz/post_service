package faang.school.postservice.exception;

public class ExternalServiceValidationException extends RuntimeException {
    public ExternalServiceValidationException(String message) {
        super(message);
    }
}
