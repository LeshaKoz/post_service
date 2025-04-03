package faang.school.postservice.exception;

public class PostNotCorrectedException extends RuntimeException {
    public PostNotCorrectedException(long postId, String action) {
        super(MessageError.POST_NOT_CORRECTED_EXCEPTION.getMessage(postId, action));
    }
}
