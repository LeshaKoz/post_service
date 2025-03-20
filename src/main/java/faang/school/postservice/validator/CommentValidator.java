package faang.school.postservice.validator;

import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommentValidator {

    private final CommentRepository commentRepository;

    public Comment getCommentById(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(
                () -> {
                    log.warn("Comment with id {} not found", commentId);
                    return new EntityNotFoundException("Comment not found");
                }
        );
    }
}
