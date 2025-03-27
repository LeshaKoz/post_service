package faang.school.postservice.service.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private CommentService commentService;

    private CommentDto inputDto;
    private UserDto userDto;
    private Comment commentEntity;
    private CommentDto expectedDto;
    private final long postId = 1L;
    private final long authorId = 2L;
    private final long commentId = 3L;

    @BeforeEach
    void setUp() {
        inputDto = CommentDto.builder()
                .postId(postId)
                .authorId(authorId)
                .content("Test content")
                .build();

        userDto = UserDto.builder()
                .id(authorId)
                .build();

        Post post = Post.builder()
                .id(postId)
                .build();

        commentEntity = Comment.builder()
                .id(commentId)
                .content("Test content")
                .authorId(authorId)
                .post(post)
                .build();

        expectedDto = CommentDto.builder()
                .id(commentId)
                .postId(postId)
                .authorId(authorId)
                .content("Test content")
                .build();
    }

    @Test
    void createComment_ShouldCreateAndReturnCommentDto_WhenValidInput() {

        when(postRepository.existsById(postId)).thenReturn(true);
        when(userServiceClient.getUser(authorId)).thenReturn(userDto);
        when(commentMapper.toEntity(inputDto)).thenReturn(commentEntity);
        when(commentRepository.save(commentEntity)).thenReturn(commentEntity);
        when(commentMapper.toDto(commentEntity)).thenReturn(expectedDto);

        CommentDto result = commentService.createComment(inputDto);

        assertNotNull(result);
        assertEquals(expectedDto, result);
        verify(postRepository).existsById(postId);
        verify(userServiceClient).getUser(authorId);
        verify(commentMapper).toEntity(inputDto);
        verify(commentRepository).save(commentEntity);
        verify(commentMapper).toDto(commentEntity);
    }

    @Test
    void createComment_ShouldThrowException_WhenPostDoesNotExist() {
        when(postRepository.existsById(postId)).thenReturn(false);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            commentService.createComment(inputDto);
        });

        assertEquals("Post with ID " + postId + " does not exist.", exception.getMessage());
        verify(postRepository).existsById(postId);
        verifyNoInteractions(userServiceClient, commentMapper, commentRepository);
    }

    @Test
    void updateComment_ShouldUpdateAndReturnCommentDto_WhenValidInput() {
        Comment existingComment = Comment.builder()
                .id(commentId)
                .authorId(authorId)
                .content("Old content")
                .post(Post.builder().id(postId).build())
                .build();

        Comment updatedComment = Comment.builder()
                .id(commentId)
                .authorId(authorId)
                .content("Updated content")
                .updatedAt(LocalDateTime.now())
                .post(Post.builder().id(postId).build())
                .build();

        CommentDto updatedDto = CommentDto.builder()
                .id(commentId)
                .authorId(authorId)
                .content("Updated content")
                .build();

        inputDto.setContent("Updated content");

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(existingComment));
        when(commentRepository.save(any(Comment.class))).thenReturn(updatedComment);
        when(commentMapper.toDto(updatedComment)).thenReturn(updatedDto);

        CommentDto result = commentService.updateComment(commentId, inputDto);

        assertNotNull(result);
        assertEquals(updatedDto, result);
        verify(commentRepository).findById(commentId);
        verify(commentRepository).save(any(Comment.class));
        verify(commentMapper).toDto(updatedComment);
    }

    @Test
    void getAllCommentsByPostId_ShouldReturnSortedComments_WhenCommentsExist() {
        Comment comment1 = Comment.builder()
                .id(1L)
                .content("First comment")
                .createdAt(LocalDateTime.of(2025, 1, 1, 10, 0))
                .post(Post.builder().id(postId).build())
                .build();

        Comment comment2 = Comment.builder()
                .id(2L)
                .content("Second comment")
                .createdAt(LocalDateTime.of(2025, 1, 2, 10, 0))
                .post(Post.builder().id(postId).build())
                .build();

        List<Comment> comments = Arrays.asList(comment1, comment2);

        CommentDto dto1 = CommentDto.builder()
                .id(1L)
                .content("First comment")
                .createdAt(LocalDateTime.of(2025, 1, 1, 10, 0))
                .build();

        CommentDto dto2 = CommentDto.builder()
                .id(2L)
                .content("Second comment")
                .createdAt(LocalDateTime.of(2025, 1, 2, 10, 0))
                .build();

        when(commentRepository.findAllByPostId(postId)).thenReturn(comments);
        when(commentMapper.toDto(comment1)).thenReturn(dto1);
        when(commentMapper.toDto(comment2)).thenReturn(dto2);

        List<CommentDto> result = commentService.getAllCommentsByPostId(postId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(dto2, result.get(0));
        assertEquals(dto1, result.get(1));
        verify(commentRepository).findAllByPostId(postId);
        verify(commentMapper).toDto(comment1);
        verify(commentMapper).toDto(comment2);
    }

    @Test
    void deleteComment_ShouldDeleteComment_WhenCommentExists() {
        when(commentRepository.existsById(commentId)).thenReturn(true);

        commentService.deleteComment(commentId);

        verify(commentRepository).existsById(commentId);
        verify(commentRepository).deleteById(commentId);
    }
}