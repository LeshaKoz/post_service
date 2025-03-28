package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentCreateDto;
import faang.school.postservice.dto.comment.CommentViewDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.comment.CommentService;
import faang.school.postservice.validation.CommentValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private CommentMapper commentMapper;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private CommentValidator commentValidator;

    @InjectMocks
    private CommentService commentService;

    private final Long postId = 1L;
    private final Long commentId = 1L;
    private final Long authorId = 1L;
    private CommentCreateDto commentCreateDto;
    private CommentViewDto commentViewDto;
    private Comment comment;
    private Post post;

    @BeforeEach
    void setUp() {
        commentCreateDto = new CommentCreateDto();
        commentCreateDto.setContent("Test content");
        commentCreateDto.setAuthorId(1L);
        commentCreateDto.setPostId(1L);

        commentViewDto = new CommentViewDto();
        commentViewDto.setId(1L);
        commentViewDto.setContent("Test content");
        commentViewDto.setAuthorId(1L);
        commentViewDto.setPostId(1L);

        post = new Post();
        post.setId(1L);

        comment = new Comment();
        comment.setId(1L);
        comment.setContent("Test content");
        comment.setAuthorId(1L);
        comment.setPost(post);
        comment.setCreatedAt(LocalDateTime.now());
    }

    @Nested
    @DisplayName("Создание комментария")
    class CreateComment {

        @Test
        @DisplayName("Успешное создание комментария")
        void givenValidCommentDataAndExistingPost_whenCreateComment_thenReturnCommentDto() {
            when(postRepository.findById(postId)).thenReturn(Optional.of(post));
            when(commentMapper.toEntity(commentCreateDto)).thenReturn(comment);
            when(commentRepository.save(comment)).thenReturn(comment);
            when(commentMapper.toViewDto(comment)).thenReturn(commentViewDto);

            CommentViewDto result = commentService.createComment(postId, commentCreateDto);

            assertNotNull(result);
            assertEquals(commentViewDto, result);
            verify(commentValidator).validateUserById(authorId);
            verify(commentRepository).save(comment);
        }

        @Test
        @DisplayName("Ошибка при несуществующем посте")
        void givenNonExistentPost_whenCreateComment_thenThrowEntityNotFoundException() {
            when(postRepository.findById(postId)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class,
                    () -> commentService.createComment(postId, commentCreateDto));
        }

        @Test
        @DisplayName("Ошибка при несуществующем авторе")
        void givenNonExistentAuthor_whenCreateComment_thenThrowEntityNotFoundException() {
            when(postRepository.findById(postId)).thenReturn(Optional.of(post));
            doThrow(new EntityNotFoundException("User not found"))
                    .when(commentValidator).validateUserById(authorId);

            assertThrows(EntityNotFoundException.class,
                    () -> commentService.createComment(postId, commentCreateDto));
        }
    }

    @Nested
    @DisplayName("Обновление комментария")
    class UpdateComment {

        @Test
        @DisplayName("Успешное обновление комментария")
        void givenValidCommentData_whenUpdateComment_thenReturnUpdatedCommentDto() {
            when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
            when(commentRepository.save(comment)).thenReturn(comment);
            when(commentMapper.toViewDto(comment)).thenReturn(commentViewDto);

            CommentViewDto result = commentService.updateComment(postId, commentId, commentCreateDto);

            assertNotNull(result);
            assertEquals(commentViewDto, result);
            assertNotNull(comment.getUpdatedAt());
            verify(commentValidator).validateCommentBelongsToPost(comment, postId, commentId);
        }

        @Test
        @DisplayName("Ошибка при несуществующем комментарии")
        void givenNonExistentComment_whenUpdateComment_thenThrowEntityNotFoundException() {
            when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class,
                    () -> commentService.updateComment(postId, commentId, commentCreateDto));
        }

        @Test
        @DisplayName("Ошибка при несоответствии postId")
        void givenMismatchedPostId_whenUpdateComment_thenThrowDataValidationException() {
            when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
            doThrow(new DataValidationException("Invalid post ID"))
                    .when(commentValidator).validateCommentBelongsToPost(comment, postId, commentId);

            assertThrows(DataValidationException.class,
                    () -> commentService.updateComment(postId, commentId, commentCreateDto));
        }
    }

    @Nested
    @DisplayName("Получение комментариев для указанного поста")
    class GetComments {

        @Test
        @DisplayName("Успешное получение списка комментариев")
        void givenPostWithComments_whenGetComments_thenReturnCommentDtoList() {
            when(commentRepository.findAllByPostId(postId)).thenReturn(List.of(comment));
            when(commentMapper.toViewDto(comment)).thenReturn(commentViewDto);

            List<CommentViewDto> result = commentService.getCommentsByPostId(postId);

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(commentViewDto, result.get(0));
        }

        @Test
        @DisplayName("Получение пустого списка комментариев")
        void givenPostWithoutComments_whenGetComments_thenReturnEmptyList() {
            when(commentRepository.findAllByPostId(postId)).thenReturn(Collections.emptyList());

            List<CommentViewDto> result = commentService.getCommentsByPostId(postId);

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("Удаление комментария")
    class DeleteComment {

        @Test
        @DisplayName("Успешное удаление комментария")
        void givenValidPostAndCommentIds_whenDeleteComment_thenSuccess() {
            when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

            assertDoesNotThrow(() -> commentService.deleteComment(postId, commentId));
            verify(commentRepository).delete(comment);
            verify(commentValidator).validateCommentBelongsToPost(comment, postId, commentId);
        }

        @Test
        @DisplayName("Ошибка при несуществующем комментарии")
        void givenNonExistentComment_whenDeleteComment_thenThrowEntityNotFoundException() {
            when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class,
                    () -> commentService.deleteComment(postId, commentId));
        }

        @Test
        @DisplayName("Ошибка при несоответствии postId")
        void givenMismatchedPostId_whenDeleteComment_thenThrowDataValidationException() {
            when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
            doThrow(new DataValidationException("Invalid post ID"))
                    .when(commentValidator).validateCommentBelongsToPost(comment, postId, commentId);

            assertThrows(DataValidationException.class,
                    () -> commentService.deleteComment(postId, commentId));
        }
    }

    @Nested
    @DisplayName("Получение комментария по ID")
    class GetCommentById {

        @Test
        @DisplayName("Успешное получение комментария по ID")
        void givenExistingCommentId_whenGetCommentById_thenReturnComment() {
            when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

            Comment result = commentService.getCommentById(commentId);

            assertNotNull(result);
            assertEquals(comment, result);
            verify(commentRepository).findById(commentId);
        }

        @Test
        @DisplayName("Ошибка при получении несуществующего комментария")
        void givenNonExistentCommentId_whenGetCommentById_thenThrowEntityNotFoundException() {
            when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                    () -> commentService.getCommentById(commentId));

            assertEquals("Comment with ID " + commentId + " not found", exception.getMessage());
            verify(commentRepository).findById(commentId);
        }

        @Test
        @DisplayName("Ошибка при передаче null в качестве ID")
        void givenNullCommentId_whenGetCommentById_thenThrowIllegalArgumentException() {
            assertThrows(EntityNotFoundException.class,
                    () -> commentService.getCommentById(null));
        }
    }
}