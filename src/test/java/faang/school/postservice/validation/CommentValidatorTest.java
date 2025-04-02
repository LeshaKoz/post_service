package faang.school.postservice.validation;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
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
    private PostRepository postRepository;

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
            when(comment.belongsToPost(postId)).thenReturn(true);

            assertDoesNotThrow(() ->
                    commentValidator.validateCommentBelongsToPost(comment, postId, commentId)
            );

            verify(comment, times(1)).belongsToPost(postId);
        }

        @Test
        @DisplayName("Должен выбросить исключение, когда комментарий не принадлежит посту")
        void givenInvalidArguments_whenValidateCommentBelongsToPost_thenThrowDataValidationException() {
            when(comment.belongsToPost(postId)).thenReturn(false);

            DataValidationException exception = assertThrows(DataValidationException.class,
                    () -> commentValidator.validateCommentBelongsToPost(comment, postId, commentId)
            );

            assertEquals("Comment with ID 1 doesn't belong to post with ID 1", exception.getMessage());
            verify(comment, times(1)).belongsToPost(postId);
        }

        @Test
        @DisplayName("Должен выбросить исключение, когда комментарий null")
        void givenNullComment_whenValidateCommentBelongsToPost_thenThrowDataValidationException() {
            DataValidationException exception = assertThrows(DataValidationException.class,
                    () -> commentValidator.validateCommentBelongsToPost(null, postId, commentId)
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

    @Nested
    @DisplayName("validatePostExists()")
    class ValidatePostExists {

        @Test
        @DisplayName("Должен пройти успешно, когда пост существует")
        void givenExistingPostId_whenValidatePostExists_thenSuccess() {
            when(postRepository.existsById(postId)).thenReturn(true);

            assertDoesNotThrow(() ->
                    commentValidator.validatePostExists(postId)
            );

            verify(postRepository, times(1)).existsById(postId);
        }

        @Test
        @DisplayName("Должен выбросить исключение, когда пост не существует")
        void givenNonExistingPostId_whenValidatePostExists_thenThrowEntityNotFoundException() {
            when(postRepository.existsById(postId)).thenReturn(false);

            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                    () -> commentValidator.validatePostExists(postId)
            );

            assertEquals("Post with ID 1 not found", exception.getMessage());
        }

        @Test
        @DisplayName("Должен выбросить исключение, когда postId равен null")
        void givenNullPostId_whenValidatePostExists_thenThrowIllegalArgumentException() {
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> commentValidator.validatePostExists(null)
            );

            assertEquals("Post ID must not be null", exception.getMessage());
        }
    }
}
