package faang.school.postservice.service;

import faang.school.postservice.dto.like.CommentLikeDto;
import faang.school.postservice.dto.like.PostLikeDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.like.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.ad.LikeRepository;
import faang.school.postservice.validator.LikeValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {

    @Mock
    private LikeRepository likeRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private LikeValidator likeValidator;
    @Mock
    private LikeMapper likeMapper;

    @InjectMocks
    private LikeService likeService;

    private Long userId;
    private Long postId;
    private Long commentId;
    private Post post;
    private Comment comment;
    private Like like;
    private PostLikeDto postLikeDto;
    private CommentLikeDto commentLikeDto;

    @BeforeEach
    void setup() {
        userId = 1L;
        postId = 10L;
        commentId = 20L;

        post = new Post();
        post.setId(postId);

        comment = new Comment();
        comment.setId(commentId);

        like = new Like();
        like.setUserId(userId);
        like.setPost(post);

        postLikeDto = new PostLikeDto();
        postLikeDto.setUserId(userId);
        postLikeDto.setPostId(postId);

        commentLikeDto = new CommentLikeDto();
        commentLikeDto.setUserId(userId);
        commentLikeDto.setCommentId(commentId);
    }

    @Test
    void likePost_ShouldValidateAndSave() {
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(likeRepository.findByPostIdAndUserId(postId, userId)).thenReturn(Optional.empty());
        when(likeMapper.toLike(postLikeDto)).thenReturn(like);

        likeService.likePost(postLikeDto);

        verify(likeValidator).validateUserExists(userId);
        verify(likeValidator).validatePostExists(postId);
        verify(likeRepository).save(any(Like.class));
    }

    @Test
    void likePost_ShouldThrowException_WhenPostNotFound() {
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> likeService.likePost(postLikeDto));

        assertEquals("Post not found with id: " + postId, exception.getMessage());
    }

    @Test
    void likePost_ShouldThrowException_WhenAlreadyLiked() {
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(likeRepository.findByPostIdAndUserId(postId, userId)).thenReturn(Optional.of(like));

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> likeService.likePost(postLikeDto));

        assertEquals("User already liked this post.", exception.getMessage());
    }

    @Test
    void unlikePost_ShouldDeleteLike() {
        likeService.unlikePost(postLikeDto);

        verify(likeRepository).deleteByPostIdAndUserId(postId, userId);
    }

    @Test
    void likeComment_ShouldValidateAndSave() {
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(likeRepository.findByCommentIdAndUserId(commentId, userId)).thenReturn(Optional.empty());
        when(likeMapper.toLike(commentLikeDto)).thenReturn(like);

        likeService.likeComment(commentLikeDto);

        verify(likeValidator).validateUserExists(userId);
        verify(likeValidator).validateCommentExists(commentId);
        verify(likeRepository).save(any(Like.class));
    }

    @Test
    void likeComment_ShouldThrowException_WhenCommentNotFound() {
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> likeService.likeComment(commentLikeDto));

        assertEquals("Comment not found with id: " + commentId, exception.getMessage());
    }

    @Test
    void likeComment_ShouldThrowException_WhenAlreadyLiked() {
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(likeRepository.findByCommentIdAndUserId(commentId, userId)).thenReturn(Optional.of(like));

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> likeService.likeComment(commentLikeDto));

        assertEquals("User already liked this comment.", exception.getMessage());
    }

    @Test
    void unlikeComment_ShouldDeleteLike() {
        likeService.unlikeComment(commentLikeDto);

        verify(likeRepository).deleteByCommentIdAndUserId(commentId, userId);
    }
}