package faang.school.postservice.exception;

public class FeedStorageException extends RuntimeException {
    public FeedStorageException(String message) {
        super(message);
    }

    public FeedStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
