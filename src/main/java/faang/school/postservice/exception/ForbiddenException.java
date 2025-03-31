package faang.school.postservice.exception;

public class ForbiddenException extends RuntimeException {

    public ForbiddenException(long userId, String action) {
        super(MessageError.FORBIDDEN_EXCEPTION.getMessage(userId, action));
    }
}
