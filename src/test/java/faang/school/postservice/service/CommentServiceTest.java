package faang.school.postservice.service;

import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentServiceImpl commentService;

    @Test
    public void testFindCommentByIdWithThrow() {
        long commentId = 1L;
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());
        Exception exception = assertThrows(DataValidationException.class,
                () -> commentService.findCommentById(commentId));
        assertEquals("Comment with id 1 not found", exception.getMessage());
        verify(commentRepository,times(1)).findById(commentId);
    }

    @Test
    public void testFindPostById() {
        long commentId = 1L;
        Comment comment = Comment.builder().id(commentId).build();
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        Comment commentResult = commentService.findCommentById(commentId);
        verify(commentRepository, times(1)).findById(commentId);
        assertEquals(commentId, commentResult.getId());
    }
}
