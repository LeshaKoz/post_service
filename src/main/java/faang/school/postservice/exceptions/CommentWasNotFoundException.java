package faang.school.postservice.exceptions;

public class CommentWasNotFoundException extends RuntimeException {
    public CommentWasNotFoundException(String message) {
        super(message);
    }
}
