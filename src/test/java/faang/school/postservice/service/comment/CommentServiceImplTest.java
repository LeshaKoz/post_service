package faang.school.postservice.service.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.exception.AuthorNotFoundException;
import faang.school.postservice.exception.CommentNotFoundException;
import faang.school.postservice.exception.PostIdMismatchException;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.comment.implementations.CommentServiceImpl;
import faang.school.postservice.service.post.interfaces.PostService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;


@Slf4j
public class CommentServiceImplTest {

    @Mock
    private PostService postService;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentServiceImpl commentService;

    private CommentDto commentDto;
    private Post post;
    private Comment comment;
    private CommentDto resultDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        post = new Post();
        post.setId(1L);
        post.setContent("Test post");
        post.setAuthorId(3L);

        comment = new Comment();
        comment.setId(1L);
        comment.setContent("Test content");
        comment.setAuthorId(2L);
        comment.setPost(post);
        comment.setCreatedAt(LocalDateTime.now());

        commentDto = new CommentDto();
        commentDto.setContent("Test content");
        commentDto.setAuthorId(2L);
        commentDto.setPostId(1L);

        resultDto = new CommentDto();
        resultDto.setId(1L);
        resultDto.setContent("Test content");
        resultDto.setAuthorId(2L);
        resultDto.setPostId(1L);
        resultDto.setCreatedAt(comment.getCreatedAt());
    }

    @Test
    void createComment_success() {
        Long postId = 1L;

        when(postService.getPostById(postId)).thenReturn(post);
        when(commentMapper.toEntity(commentDto)).thenReturn(comment);
        when(commentRepository.save(comment)).thenReturn(comment);
        when(commentMapper.toDto(comment)).thenReturn(resultDto);

        CommentDto result = commentService.createComment(postId, commentDto);

        assertNotNull(result);
        assertEquals(resultDto, result);
        verify(postService).getPostById(postId);
        verify(userServiceClient).getUser(2L);
        verify(commentRepository).save(comment);
    }

    @Test
    void createComment_postIdMismatch_throwsException() {
        Long postId = 1L;

        commentDto.setPostId(3L);

        PostIdMismatchException exception = assertThrows(PostIdMismatchException.class,
                () -> commentService.createComment(postId, commentDto));
        assertEquals("Post ID in path and DTO must match", exception.getMessage());
        verifyNoInteractions(postService, userServiceClient, commentRepository, commentMapper);
    }

    @Test
    void updateComment_success() {
        Long postId = 1L;
        Long commentId = 1L;

        CommentDto updateDto = new CommentDto();
        updateDto.setId(commentId);
        updateDto.setContent("Updated content");
        updateDto.setAuthorId(2L);
        updateDto.setPostId(postId);

        Comment updatedComment = new Comment();
        updatedComment.setId(commentId);
        updatedComment.setContent("Updated content");
        updatedComment.setAuthorId(2L);
        updatedComment.setPost(post);
        updatedComment.setCreatedAt(comment.getCreatedAt());

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(commentRepository.save(comment)).thenReturn(updatedComment);
        when(commentMapper.toDto(updatedComment)).thenReturn(resultDto);

        CommentDto result = commentService.updateComment(postId, commentId, updateDto);

        assertNotNull(result);
        assertEquals(resultDto, result);
        assertEquals("Updated content", comment.getContent());
        verify(commentRepository).findById(commentId);
        verify(userServiceClient).getUser(2L);
        verify(commentRepository).save(comment);
    }

    @Test
    void updateComment_commentNotFound_throwsException() {
        Long postId = 1L;
        Long commentId = 1L;

        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        CommentNotFoundException exception = assertThrows(CommentNotFoundException.class,
                () -> commentService.updateComment(postId, commentId, commentDto));
        assertEquals("Comment with id " + commentId + " not found", exception.getMessage());
        verify(commentRepository).findById(commentId);
        verifyNoMoreInteractions(commentRepository, userServiceClient, postService, commentMapper);
    }

    @Test
    void getCommentsByPostId_success() {
        Long postId = 1L;

        Comment comment1 = new Comment();
        comment1.setId(1L);
        comment1.setContent("First");
        comment1.setAuthorId(2L);
        comment1.setPost(post);
        comment1.setCreatedAt(LocalDateTime.now().minusDays(1));

        Comment comment2 = new Comment();
        comment2.setId(2L);
        comment2.setContent("Second");
        comment2.setAuthorId(2L);
        comment2.setPost(post);
        comment2.setCreatedAt(LocalDateTime.now());

        List<Comment> comments = List.of(comment1, comment2);

        CommentDto dto1 = new CommentDto();
        dto1.setId(1L);
        dto1.setContent("First");
        dto1.setAuthorId(2L);
        dto1.setPostId(postId);
        dto1.setCreatedAt(comment1.getCreatedAt());

        CommentDto dto2 = new CommentDto();
        dto2.setId(2L);
        dto2.setContent("Second");
        dto2.setAuthorId(2L);
        dto2.setPostId(postId);
        dto2.setCreatedAt(comment2.getCreatedAt());

        when(commentRepository.findAllByPostId(postId)).thenReturn(comments);
        when(commentMapper.toDto(comment1)).thenReturn(dto1);
        when(commentMapper.toDto(comment2)).thenReturn(dto2);

        List<CommentDto> result = commentService.getCommentsByPostId(postId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(dto2, result.get(0));
        assertEquals(dto1, result.get(1));
        verify(commentRepository).findAllByPostId(postId);
    }

    @Test
    void getCommentsByPostId_emptyList() {
        Long postId = 1L;

        when(commentRepository.findAllByPostId(postId)).thenReturn(Collections.emptyList());

        List<CommentDto> result = commentService.getCommentsByPostId(postId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(commentRepository).findAllByPostId(postId);
        verifyNoInteractions(commentMapper);
    }

    @Test
    void deleteComment_success() {
        Long postId = 1L;
        Long commentId = 1L;

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        doNothing().when(commentRepository).delete(comment);

        commentService.deleteComment(postId, commentId);

        verify(commentRepository).findById(commentId);
        verify(userServiceClient).getUser(2L);
        verify(commentRepository).delete(comment);
    }

    @Test
    void deleteComment_authorNotFound_throwsException() {
        Long postId = 1L;
        Long commentId = 1L;

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        doThrow(new AuthorNotFoundException("Author with id 2 not found"))
                .when(userServiceClient).getUser(2L);

        AuthorNotFoundException exception = assertThrows(AuthorNotFoundException.class,
                () -> commentService.deleteComment(postId, commentId));
        assertEquals("Author with id 2 not found", exception.getMessage());
        verify(commentRepository).findById(commentId);
        verify(userServiceClient).getUser(2L);
        verifyNoMoreInteractions(commentRepository);
    }
}
