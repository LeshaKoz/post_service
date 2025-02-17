package faang.school.postservice.exception.album;

public class AlbumAccessDeniedException extends RuntimeException {
    public AlbumAccessDeniedException(String message) {
        super(message);
    }
}
