package faang.school.postservice.exception;

public class FileEmptyException extends RuntimeException{
    public FileEmptyException(String message) {
        super(message);
    }
}
