package faang.school.postservice.exception;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(long entityId) {
        super("Entity with id " + entityId + " not found");
    }
}
