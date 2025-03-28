package faang.school.postservice.exceptions;

public class PostAlreadyPublishedException extends RuntimeException {
    public PostAlreadyPublishedException(String message) {
        super(message);
    }
}

