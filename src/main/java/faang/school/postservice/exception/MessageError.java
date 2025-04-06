package faang.school.postservice.exception;

import lombok.Getter;

@Getter
public enum MessageError {
    POST_NOT_FOUND_EXCEPTION("Post with id %d not found"),
    AUTHOR_NOT_FOUND_EXCEPTION("Author with id %d not found"),
    COMMENT_NOT_FOUND_EXCEPTION("Comment with id %d not found"),
    FORBIDDEN_EXCEPTION("User with id %d is not allowed to %s"),
    ENTITY_NOT_FOUND_EXCEPTION("Entity %s with id %d was not found");

    private final String message;

    MessageError(String message) {
        this.message = message;
    }

    public String getMessage(Object... args) {
        return String.format(message, args);
    }
}
