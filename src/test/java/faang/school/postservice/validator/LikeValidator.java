package faang.school.postservice.validator;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LikeValidatorTest {

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private LikeValidator likeValidator;

    @Test
    void validateUserExists_ShouldNotThrowException_WhenUserExists() {
        long userId = 1L;

        when(userServiceClient.getUser(userId)).thenReturn(null);

        assertDoesNotThrow(() -> likeValidator.validateUserExists(userId));

        verify(userServiceClient, times(1)).getUser(userId);
    }

    @Test
    void validatePostExists_ShouldNotThrowException_WhenPostExists() {
        Long postId = 1L;

        when(postRepository.existsById(postId)).thenReturn(true);

        assertDoesNotThrow(() -> likeValidator.validatePostExists(postId));

        verify(postRepository, times(1)).existsById(postId);
    }

    @Test
    void validatePostExists_ShouldThrowException_WhenPostDoesNotExist() {
        Long postId = 1L;

        when(postRepository.existsById(postId)).thenReturn(false);

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> likeValidator.validatePostExists(postId));

        assertEquals("Post with id " + postId + " not found.", exception.getMessage());

        verify(postRepository, times(1)).existsById(postId);
    }

    @Test
    void validatePostExists_ShouldThrowException_WhenPostIdIsNull() {
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> likeValidator.validatePostExists(null));

        assertEquals("Post with id null not found.", exception.getMessage());
    }

    @Test
    void validateCommentExists_ShouldNotThrowException_WhenCommentExists() {
        Long commentId = 1L;

        when(commentRepository.existsById(commentId)).thenReturn(true);

        assertDoesNotThrow(() -> likeValidator.validateCommentExists(commentId));

        verify(commentRepository, times(1)).existsById(commentId);
    }

    @Test
    void validateCommentExists_ShouldThrowException_WhenCommentDoesNotExist() {
        Long commentId = 1L;

        when(commentRepository.existsById(commentId)).thenReturn(false);

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> likeValidator.validateCommentExists(commentId));

        assertEquals("Comment with id " + commentId + " not found.", exception.getMessage());

        verify(commentRepository, times(1)).existsById(commentId);
    }

    @Test
    void validateCommentExists_ShouldThrowException_WhenCommentIdIsNull() {
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> likeValidator.validateCommentExists(null));

        assertEquals("Comment with id null not found.", exception.getMessage());
    }
}