package faang.school.postservice.exception;

public class InvalidPostEventException extends RuntimeException {
    public InvalidPostEventException(String message) {
        super(message);
    }
}
