package faang.school.postservice.exception;

public class DuplicateEntityException extends RuntimeException {

    public DuplicateEntityException(String message, Object... args) {
        super(String.format(message, args));
    }
}
