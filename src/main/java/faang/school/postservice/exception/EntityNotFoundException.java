package faang.school.postservice.exception;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String message, String entityName, Long entityId) {
        super(String.format(message, entityName, entityId));
    }

    public EntityNotFoundException(String message, String entityName, String subEntityName, Long entityId) {
        super(String.format(message, entityName, subEntityName, entityId));
    }
}
