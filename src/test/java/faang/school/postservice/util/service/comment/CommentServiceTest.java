package faang.school.postservice.util.service.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentCreateDto;
import faang.school.postservice.dto.comment.CommentViewDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.comment.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
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

    @InjectMocks
    private CommentService commentService;

    private CommentCreateDto commentCreateDto;
    private CommentViewDto commentViewDto;
    private Comment comment;
    private Post post;
    private UserDto userDto;

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

        userDto = new UserDto(1L, "test_user", "test@example.com");
    }

    @Nested
    @DisplayName("Tests for createComment method")
    class CreateCommentTests {

        @Test
        @DisplayName("Should successfully create comment with valid data")
        void createComment_WithValidData_ShouldReturnCommentViewDto() {
            when(postRepository.findById(1L)).thenReturn(Optional.of(post));
            when(userServiceClient.getUser(1L)).thenReturn(userDto);
            when(commentMapper.toEntity(commentCreateDto)).thenReturn(comment);
            when(commentRepository.save(any(Comment.class))).thenReturn(comment);
            when(commentMapper.toViewDto(comment)).thenReturn(commentViewDto);

            CommentViewDto result = commentService.createComment(1L, commentCreateDto);

            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals("Test content", result.getContent());
            verify(commentRepository, times(1)).save(comment);
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when post doesn't exist")
        void createComment_WithNonExistingPost_ShouldThrowException() {
            when(postRepository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class,
                    () -> commentService.createComment(1L, commentCreateDto));
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when author doesn't exist")
        void createComment_WithNonExistingAuthor_ShouldThrowException() {
            when(postRepository.findById(1L)).thenReturn(Optional.of(post));
            when(userServiceClient.getUser(1L)).thenReturn(null);

            assertThrows(EntityNotFoundException.class,
                    () -> commentService.createComment(1L, commentCreateDto));
        }
    }

    @Nested
    @DisplayName("Tests for updateComment method")
    class UpdateCommentTests {

        @Test
        @DisplayName("Should successfully update comment with valid data")
        void updateComment_WithValidData_ShouldReturnUpdatedComment() {
            when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
            when(commentRepository.save(any(Comment.class))).thenReturn(comment);
            when(commentMapper.toViewDto(comment)).thenReturn(commentViewDto);

            CommentViewDto result = commentService.updateComment(1L, 1L, commentCreateDto);

            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertNotNull(comment.getUpdatedAt());
            verify(commentRepository, times(1)).save(comment);
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when comment doesn't exist")
        void updateComment_WithNonExistingComment_ShouldThrowException() {
            when(commentRepository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class,
                    () -> commentService.updateComment(1L, 1L, commentCreateDto));
        }

        @Test
        @DisplayName("Should throw DataValidationException when postId doesn't match")
        void updateComment_WithMismatchedPostId_ShouldThrowException() {
            comment.getPost().setId(2L);
            when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

            assertThrows(DataValidationException.class,
                    () -> commentService.updateComment(1L, 1L, commentCreateDto));
        }
    }

    @Nested
    @DisplayName("Tests for getCommentsByPostId method")
    class GetCommentsTests {

        @Test
        @DisplayName("Should return list of comments for existing post")
        void getCommentsByPostId_WithExistingPost_ShouldReturnCommentList() {
            when(commentRepository.findAllByPostId(1L)).thenReturn(List.of(comment));
            when(commentMapper.toViewDto(comment)).thenReturn(commentViewDto);

            List<CommentViewDto> result = commentService.getCommentsByPostId(1L);

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(1L, result.get(0).getId());
        }

        @Test
        @DisplayName("Should return empty list when post has no comments")
        void getCommentsByPostId_WithNoComments_ShouldReturnEmptyList() {
            when(commentRepository.findAllByPostId(1L)).thenReturn(List.of());

            List<CommentViewDto> result = commentService.getCommentsByPostId(1L);

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("Tests for deleteComment method")
    class DeleteCommentTests {

        @Test
        @DisplayName("Should successfully delete comment with valid ids")
        void deleteComment_WithValidIds_ShouldDeleteComment() {
            when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

            commentService.deleteComment(1L, 1L);

            verify(commentRepository, times(1)).delete(comment);
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when comment doesn't exist")
        void deleteComment_WithNonExistingComment_ShouldThrowException() {
            when(commentRepository.findById(1L)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class,
                    () -> commentService.deleteComment(1L, 1L));
        }

        @Test
        @DisplayName("Should throw DataValidationException when postId doesn't match")
        void deleteComment_WithMismatchedPostId_ShouldThrowException() {
            comment.getPost().setId(2L);
            when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

            assertThrows(DataValidationException.class,
                    () -> commentService.deleteComment(1L, 1L));
        }
    }
}
