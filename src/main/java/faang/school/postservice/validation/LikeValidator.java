package faang.school.postservice.validation;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LikeValidator {

    private final UserServiceClient userServiceClient;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public void validateUserExists(Long userId) {
        userServiceClient.getUser(userId);
    }

    public void validatePostExists(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new DataValidationException("Post with id " + postId + " not found.");
        }
    }

    public void validateCommentExists(Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new DataValidationException("Comment with id " + commentId + " not found.");
        }
    }
}