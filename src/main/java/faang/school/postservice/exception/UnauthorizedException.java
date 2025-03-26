package faang.school.postservice.exception;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(long userId, Exception e) {
        super("Can not authorized user with id " + userId, e);
    }
}
