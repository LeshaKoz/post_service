package faang.school.postservice.exception;

public class FileSizeValidationException extends RuntimeException {
    public FileSizeValidationException(String message) {
        super(message);
    }
}
