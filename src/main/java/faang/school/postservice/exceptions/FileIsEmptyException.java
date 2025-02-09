package faang.school.postservice.exceptions;

public class FileIsEmptyException extends RuntimeException {
    public FileIsEmptyException(String s) {
        super(s);
    }
}
