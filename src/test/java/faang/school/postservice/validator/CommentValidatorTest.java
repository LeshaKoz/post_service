package faang.school.postservice.validator;

import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentValidatorTest {

    private static final Long COMMENT_ID = 1L;

    @Mock
    private CommentRepository commentRepository;
    @InjectMocks
    private CommentValidator commentValidator;

    @Test
    public void testGetByIdCommentNotFound() {
        String errorMessage = "Comment not found";
        when(commentRepository.findById(COMMENT_ID))
                .thenThrow(new EntityNotFoundException(errorMessage));

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> commentValidator.getCommentById(COMMENT_ID));
        verify(commentRepository, times(1)).findById(COMMENT_ID);
        assertEquals(errorMessage, exception.getMessage());
    }

    @Test
    public void testGetCommentById() {
        Comment comment = Comment.builder()
                .id(COMMENT_ID)
                .content("content")
                .build();

        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(comment));
        Comment result = commentValidator.getCommentById(COMMENT_ID);

        verify(commentRepository, times(1)).findById(COMMENT_ID);
        assertNotNull(result);
        assertEquals(comment.getId(), result.getId());
        assertEquals(comment.getContent(), result.getContent());
    }
}
