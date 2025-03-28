package faang.school.postservice.validation;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentValidatorTest {

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private Comment comment;

    @Mock
    private Post post;

    @InjectMocks
    private CommentValidator commentValidator;

    private final Long postId = 1L;
    private final Long commentId = 1L;
    private final Long authorId = 1L;

    @Nested
    @DisplayName("validateCommentBelongsToPost()")
    class ValidateCommentBelongsToPost {

        @Test
        @DisplayName("Должен пройти успешно, когда комментарий принадлежит посту")
        void givenValidArguments_whenValidateCommentBelongsToPost_thenSuccess() {
            when(comment.getPost()).thenReturn(post);
            when(post.getId()).thenReturn(postId);

            assertDoesNotThrow(() ->
                    commentValidator.validateCommentBelongsToPost(comment, postId, commentId)
            );

            verify(comment, times(1)).getPost();
            verify(post, times(1)).getId();
        }

        @Test
        @DisplayName("Должен выбросить исключение, когда комментарий не принадлежит посту")
        void givenInvalidArguments_whenValidateCommentBelongsToPost_thenThrowDataValidationException() {
            when(comment.getPost()).thenReturn(post);
            when(post.getId()).thenReturn(999L);

            DataValidationException exception = assertThrows(DataValidationException.class,
                    () -> commentValidator.validateCommentBelongsToPost(comment, postId, commentId)
            );

            assertEquals("Comment with ID 1 doesn't belong to post with ID 1", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("validateUserById()")
    class ValidateUserById {

        @Test
        @DisplayName("Проверка существования пользователя")
        void givenValidUserId_whenValidateUserById_thenSuccess() {
            when(userServiceClient.getUser(authorId))
                    .thenReturn(new UserDto(1L, "username", "email@gmail.com"));

            assertDoesNotThrow(() ->
                    commentValidator.validateUserById(authorId)
            );

            verify(userServiceClient, times(1)).getUser(authorId);
        }
    }

    @Test
    @DisplayName("Должен выбросить исключение, когда пользователь не существует")
    void givenInvalidUserId_whenValidateUserById_thenThrowEntityNotFoundException() {
        when(userServiceClient.getUser(authorId)).thenReturn(null);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> commentValidator.validateUserById(authorId)
        );

        assertEquals("User with ID 1 not found", exception.getMessage());
    }
}
