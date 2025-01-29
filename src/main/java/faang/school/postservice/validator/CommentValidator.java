package faang.school.postservice.validator;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.UpdateCommentRequest;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentValidator {
    private final UserServiceClient userServiceClient;

    public void verificationCreatingData(Post post) {
        if (userServiceClient.getUser(post.getAuthorId()) == null) {
            throw new EntityNotFoundException(String.format("User with id: %s not found",
                    post.getAuthorId()));
        }
    }

    public void validateForUpdate(Comment comment, UpdateCommentRequest request) {
        if (comment.getContent().equals(request.getContent())) {
            throw new IllegalArgumentException("The comment has not been changed.");
        }
    }
}
