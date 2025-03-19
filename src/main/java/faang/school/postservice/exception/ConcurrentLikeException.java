package faang.school.postservice.exception;

public class ConcurrentLikeException extends RuntimeException {

    public ConcurrentLikeException(String message, String subEntityName, String entityName, Long entityId) {
        super(String.format(message, subEntityName, entityName, entityId));
    }
}
