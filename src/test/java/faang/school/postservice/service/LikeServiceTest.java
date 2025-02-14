package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.like.CommentLikeDto;
import faang.school.postservice.dto.like.PostLikeDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.like.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.ad.LikeRepository;
import faang.school.postservice.validator.LikeValidator;
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
    private PostService postService;
    @Mock
    private CommentService commentService;
    @Mock
    private LikeValidator likeValidator;
    @Mock
    private LikeMapper likeMapper;
    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private LikeService likeService;

    private UserDto testUser;
    private Long postId;
    private Long commentId;
    private Post testPost;
    private Comment testComment;
    private Like testLike;
    private PostLikeDto postLikeDto;
    private CommentLikeDto commentLikeDto;

    @BeforeEach
    void setup() {
        testUser = new UserDto(1L, "Test User", "test@example.com");
        postId = 10L;
        commentId = 20L;

        testPost = new Post();
        testPost.setId(postId);

        testComment = new Comment();
        testComment.setId(commentId);

        testLike = new Like();
        testLike.setUserId(testUser.id());
        testLike.setPost(testPost);

        postLikeDto = new PostLikeDto();
        postLikeDto.setUserId(testUser.id());
        postLikeDto.setPostId(postId);

        commentLikeDto = new CommentLikeDto();
        commentLikeDto.setUserId(testUser.id());
        commentLikeDto.setCommentId(commentId);
    }

    @Test
    void likePost_ShouldValidateAndSave() {
        when(userServiceClient.getUser(postLikeDto.getUserId())).thenReturn(testUser);
        when(postService.getPostById(postId)).thenReturn(testPost);
        when(likeRepository.findByPostIdAndUserId(postId, testUser.id())).thenReturn(Optional.empty());
        when(likeMapper.toLike(postLikeDto)).thenReturn(testLike);

        assertDoesNotThrow(() -> likeService.likePost(postLikeDto));

        verify(likeValidator).validateUserExists(testUser);
        verify(likeValidator).validatePostExists(testPost);
        verify(likeRepository).save(any(Like.class));
    }

    @Test
    void likePost_ShouldThrowException_WhenUserNotFound() {
        when(userServiceClient.getUser(postLikeDto.getUserId())).thenReturn(null);

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> likeService.likePost(postLikeDto));

        assertEquals("User not found.", exception.getMessage());
    }

    @Test
    void likePost_ShouldThrowException_WhenPostNotFound() {
        when(userServiceClient.getUser(postLikeDto.getUserId())).thenReturn(testUser);
        when(userServiceClient.getUser(anyLong())).thenReturn(null);

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> likeService.likePost(postLikeDto));

        assertEquals("Post not found.", exception.getMessage());
    }

    @Test
    void likePost_ShouldThrowException_WhenAlreadyLiked() {
        when(userServiceClient.getUser(postLikeDto.getUserId())).thenReturn(testUser);
        when(postService.getPostById(postId)).thenReturn(testPost);
        when(likeRepository.findByPostIdAndUserId(postId, testUser.id())).thenReturn(Optional.of(testLike));

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> likeService.likePost(postLikeDto));

        assertEquals("User already liked this post.", exception.getMessage());
    }

    @Test
    void unlikePost_ShouldDeleteLike() {
        when(userServiceClient.getUser(postLikeDto.getUserId())).thenReturn(testUser);
        when(postService.getPostById(postId)).thenReturn(testPost);

        assertDoesNotThrow(() -> likeService.unlikePost(postLikeDto));

        verify(likeRepository).deleteByPostIdAndUserId(postId, testUser.id());
    }

    @Test
    void likeComment_ShouldValidateAndSave() {
        when(userServiceClient.getUser(commentLikeDto.getUserId())).thenReturn(testUser);
        when(commentService.getCommentById(commentId)).thenReturn(testComment);
        when(likeRepository.findByCommentIdAndUserId(commentId, testUser.id())).thenReturn(Optional.empty());
        when(likeMapper.toLike(commentLikeDto)).thenReturn(testLike);

        assertDoesNotThrow(() -> likeService.likeComment(commentLikeDto));

        verify(likeValidator).validateUserExists(testUser);
        verify(likeValidator).validateCommentExists(testComment);
        verify(likeRepository).save(any(Like.class));
    }

    @Test
    void likeComment_ShouldThrowException_WhenUserNotFound() {
        when(userServiceClient.getUser(commentLikeDto.getUserId())).thenReturn(null);

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> likeService.likeComment(commentLikeDto));

        assertEquals("User not found.", exception.getMessage());
    }

    @Test
    void likeComment_ShouldThrowException_WhenCommentNotFound() {
        when(userServiceClient.getUser(commentLikeDto.getUserId())).thenReturn(testUser);
        when(commentService.getCommentById(commentId)).thenReturn(null);

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> likeService.likeComment(commentLikeDto));

        assertEquals("Comment not found.", exception.getMessage());
    }

    @Test
    void likeComment_ShouldThrowException_WhenAlreadyLiked() {
        when(userServiceClient.getUser(commentLikeDto.getUserId())).thenReturn(testUser);
        when(commentService.getCommentById(commentId)).thenReturn(testComment);
        when(likeRepository.findByCommentIdAndUserId(commentId, testUser.id())).thenReturn(Optional.of(testLike));

        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> likeService.likeComment(commentLikeDto));

        assertEquals("User already liked this comment.", exception.getMessage());
    }

    @Test
    void unlikeComment_ShouldDeleteLike() {
        when(userServiceClient.getUser(commentLikeDto.getUserId())).thenReturn(testUser);
        when(commentService.getCommentById(commentId)).thenReturn(testComment);

        assertDoesNotThrow(() -> likeService.unlikeComment(commentLikeDto));

        verify(likeRepository).deleteByCommentIdAndUserId(commentId, testUser.id());
    }
}