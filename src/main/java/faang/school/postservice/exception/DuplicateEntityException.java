package faang.school.postservice.exception;

public class DuplicateEntityException extends RuntimeException {

    public DuplicateEntityException(String message, String nameNotFoundEntity, String nameTargetEntity, Long entityId) {
        super(String.format(message, nameNotFoundEntity, nameTargetEntity, entityId));
    }
}
