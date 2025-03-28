package faang.school.postservice.service;

import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {
    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentService commentService;

    private final Long commentId = 1L;
    private final Comment comment = new Comment();

    @Test
    @DisplayName("getComment: позитивный сценарий")
    public void givenExistingCommentIdWhenGetCommentEntityThenReturnCommentEntity() {
        Mockito.when(commentRepository.findById(commentId))
                .thenReturn(Optional.of(comment));

        Comment returnedComment = commentService.getCommentEntity(commentId);

        Assertions.assertNotNull(returnedComment);
    }

    @Test
    @DisplayName("getComment: комментарий не найден")
    public void givenNonExistingCommentIdWhenGetCommentEntityThenThrowEntityNotFoundException() {
        Mockito.when(commentRepository.findById(commentId))
                .thenReturn(Optional.empty());

        Exception exception = Assertions.assertThrows(EntityNotFoundException.class, () ->
                commentService.getCommentEntity(commentId));

        Assertions.assertEquals("Comment not found with id: 1", exception.getMessage());
    }
}
