package faang.school.postservice.validator;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.UpdateCommentRequest;
import faang.school.postservice.model.Comment;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentValidator {
    private final UserServiceClient userServiceClient;

    public void verificationCreatingData(Comment comment) {
        if (userServiceClient.getUser(comment.getAuthorId()) == null) {
            throw new EntityNotFoundException("you are not registered in our registered!");
        }
        if (comment.getContent().isBlank()) {
            throw new IllegalArgumentException("A comment cannot be empty!");
        }
    }

    public void validateForUpdate(Comment comment, UpdateCommentRequest request) {
        if (comment.getContent().equals(request.getContent())) {
            throw new IllegalArgumentException("The comment has not been changed.");
        }
    }
}
