package faang.school.postservice.exception;

public class AlbumAccessDeniedException extends RuntimeException {

    public AlbumAccessDeniedException(String message) {
        super(message);
    }
}
