package faang.school.postservice.util.service.like;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.service.comment.CommentService;
import faang.school.postservice.service.like.LikeService;
import faang.school.postservice.service.post.PostService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {

    @Mock
    private UserContext userContext;

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private PostService postService;

    @Mock
    private CommentService commentService;

    @InjectMocks
    private LikeService likeService;

    private UserDto userDto;
    private long userId;
    private long postId;
    private long commentId;
    private Post post;
    private Like like;

    @BeforeEach
    void setUp() {
        commentId = 1L;
        postId = 1L;
        userId = 1L;
        post = new Post();
        post.setId(postId);

        like = new Like();
        like.setPost(post);
        like.setUserId(userId);
        userDto = new UserDto(1L, "david", "@mail.ru");
    }

    @Test
    void testSaveLikePost() {
        when(postService.getPostById(postId)).thenReturn(post);
        when(userServiceClient.getUser(userId)).thenReturn(userDto);
        when(likeRepository.findByPostIdAndUserId(postId, userId)).thenReturn(Optional.empty());
        when(likeRepository.save(any(Like.class))).thenReturn(like);

        Like result = likeService.saveLikePost(postId, userId);

        assertEquals(like, result);
        verify(likeRepository, times(1)).save(any(Like.class));
    }

    @Test
    void testSaveLikePost_UserNotFound() {
        when(userServiceClient.getUser(userId)).thenThrow(
            new EntityNotFoundException("User not found"));

        assertThrows(EntityNotFoundException.class, () -> likeService.saveLikePost(postId, userId));
    }

    @Test
    void testSaveLikePost_LikeAlreadyExists() {
        when(postService.getPostById(postId)).thenReturn(post);
        when(userServiceClient.getUser(userId)).thenReturn(userDto);
        when(likeRepository.findByPostIdAndUserId(postId, userId)).thenReturn(
            Optional.of(new Like()));

        assertThrows(EntityExistsException.class, () -> likeService.saveLikePost(postId, userId));
    }

    @Test
    void testSaveLikeComment() {
        Comment comment = new Comment();
        comment.setId(commentId);
        Like like = new Like();
        like.setComment(comment);
        like.setUserId(userId);

        when(userServiceClient.getUser(userId)).thenReturn(userDto);
        when(likeRepository.findByCommentIdAndUserId(commentId, userId)).thenReturn(
            Optional.empty());
        when(commentService.getComment(commentId)).thenReturn(comment);
        when(likeRepository.save(any(Like.class))).thenReturn(like);

        Like result = likeService.saveLikeComment(commentId, userId);

        assertNotNull(result);
        assertEquals(commentId, result.getComment().getId());
        assertEquals(userId, result.getUserId());
    }

    @Test
    void testSaveLikeComment_UserNotFound() {
        when(userServiceClient.getUser(userId)).thenThrow(
            new EntityNotFoundException("User not found"));

        assertThrows(EntityNotFoundException.class,
            () -> likeService.saveLikeComment(commentId, userId));
    }

    @Test
    void testSaveLikeComment_LikeAlreadyExists() {
        when(userServiceClient.getUser(userId)).thenReturn(userDto);
        when(likeRepository.findByCommentIdAndUserId(commentId, userId)).thenReturn(
            Optional.of(new Like()));

        assertThrows(EntityExistsException.class,
            () -> likeService.saveLikeComment(commentId, userId));
    }

    @Test
    void testDeleteLikePost() {
        when(userServiceClient.getUser(userId)).thenReturn(userDto);
        when(likeRepository.findByPostIdAndUserId(postId, userId)).thenReturn(
            Optional.of(new Like()));

        likeService.deleteLikePost(postId, userId);

        verify(likeRepository, times(1)).deleteByPostIdAndUserId(postId, userId);
    }

    @Test
    void testDeleteLikePost_LikeNotFound() {
        when(userServiceClient.getUser(userId)).thenReturn(userDto);
        when(likeRepository.findByPostIdAndUserId(postId, userId)).thenReturn(Optional.empty());

        assertThrows(EntityExistsException.class, () -> likeService.deleteLikePost(postId, userId));
    }

    @Test
    void testDeleteLikeComment() {
        when(userServiceClient.getUser(userId)).thenReturn(userDto);
        when(likeRepository.findByCommentIdAndUserId(commentId, userId)).thenReturn(
            Optional.of(new Like()));

        likeService.deleteLikeComment(commentId, userId);

        verify(likeRepository, times(1)).deleteByCommentIdAndUserId(commentId, userId);
    }

    @Test
    void testDeleteLikeComment_LikeNotFound() {
        when(userServiceClient.getUser(userId)).thenReturn(userDto);
        when(likeRepository.findByCommentIdAndUserId(commentId, userId)).thenReturn(
            Optional.empty());

        assertThrows(EntityExistsException.class,
            () -> likeService.deleteLikeComment(commentId, userId));
    }

    @Test
    void testCountLikesPost() {
        Post post = new Post();
        post.setId(postId);
        post.setPublished(true);
        post.setLikes(List.of(new Like(), new Like()));

        when(postService.getPostById(postId)).thenReturn(post);

        Long result = likeService.countLikesPost(postId);

        assertEquals(2L, result);
    }

    @Test
    void testCountLikesPost_PostNotPublished() {
        Post post = new Post();
        post.setId(postId);
        post.setPublished(false);

        when(postService.getPostById(postId)).thenReturn(post);

        assertThrows(IllegalArgumentException.class, () -> likeService.countLikesPost(postId));
    }

    @Test
    void testLikesPost() {
        Post post = new Post();
        post.setId(postId);
        Like like1 = new Like();
        like1.setUserId(101L);
        Like like2 = new Like();
        like2.setUserId(102L);
        post.setLikes(List.of(like1, like2));

        UserDto user1 = new UserDto(101L, "user1", "user1@example.com");
        UserDto user2 = new UserDto(102L, "user2", "user2@example.com");

        when(postService.getPostById(postId)).thenReturn(post);
        when(userServiceClient.getUser(101L)).thenReturn(user1);
        when(userServiceClient.getUser(102L)).thenReturn(user2);

        List<String> result = likeService.likesPost(postId);

        assertEquals(2, result.size());
        assertEquals("user1", result.get(0));
        assertEquals("user2", result.get(1));
    }

    @Test
    void testLikesPost_PostNotFound() {
        when(postService.getPostById(postId)).thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> likeService.likesPost(postId));
    }

    @Test
    void testLikesPost_UserNotFound() {
        Post post = new Post();
        post.setId(postId);
        Like like1 = new Like();
        like1.setUserId(101L);
        Like like2 = new Like();
        like2.setUserId(102L);
        post.setLikes(List.of(like1, like2));

        when(postService.getPostById(postId)).thenReturn(post);
        when(userServiceClient.getUser(101L)).thenReturn(null);
        when(userServiceClient.getUser(102L)).thenReturn(null);

        List<String> result = likeService.likesPost(postId);

        assertEquals(0, result.size());
    }
}