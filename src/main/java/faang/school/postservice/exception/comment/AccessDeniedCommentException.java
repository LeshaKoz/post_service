package faang.school.postservice.exception.comment;

public class AccessDeniedCommentException extends RuntimeException {
    public AccessDeniedCommentException(String message) {
        super(message);
    }
}
