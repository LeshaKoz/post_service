package faang.school.postservice;

public class DataValidationException extends RuntimeException {

    public DataValidationException(String message) {
        super(message);
    }

    public DataValidationException(String message, Object... args) {
        super(String.format(message, args));
    }
}
