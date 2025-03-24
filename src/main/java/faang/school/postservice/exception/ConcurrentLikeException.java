package faang.school.postservice.exception;

public class ConcurrentLikeException extends RuntimeException {

    public ConcurrentLikeException(String message, Object... args) {
        super(String.format(message, args));
    }
}
