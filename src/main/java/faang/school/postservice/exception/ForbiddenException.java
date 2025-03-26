package faang.school.postservice.exception;

public class ForbiddenException extends RuntimeException {
    public ForbiddenException(long userId) {
        super("User with ID " + userId + " can not do this action");
    }
}
