package faang.school.postservice.exception;

public class MinioFileNotFoundException extends RuntimeException{
    public MinioFileNotFoundException(String message) {
        super(message);
    }
}
