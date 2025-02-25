package faang.school.postservice.service;

import faang.school.postservice.broker.KafkaProducerLikeService;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.like.LikeCommentRequest;
import faang.school.postservice.dto.like.LikePostEvent;
import faang.school.postservice.dto.like.LikePostRequest;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exceptions.CommentWasNotFoundException;
import faang.school.postservice.exceptions.PostWasNotFoundException;
import faang.school.postservice.exceptions.UserServiceConnectException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LikeServiceTest {
    @Mock
    private LikeRepository likeRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private PostService postService;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private CommentService commentService;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private KafkaProducerLikeService kafkaProducer;
    @InjectMocks
    private LikeService likeService;

    private List<UserDto> userList;
    private Stream<Like> likeStream;
    private Post post;
    private Comment comment;
    private UserDto userDto;

    @BeforeEach
    public void setUp() {
        post = Post.builder()
                .id(1L)
                .authorId(13L)
                .likes(new ArrayList<>())
                .createdAt(LocalDateTime.now())
                .build();
        comment = Comment.builder()
                .id(1L)
                .authorId(144L)
                .createdAt(LocalDateTime.now())
                .build();
        userDto = UserDto.builder()
                .id(1L)
                .username("Bob")
                .build();


        likeStream = Stream.of(
                Like.builder().id(1).userId(1L).build(),
                Like.builder().id(2).userId(2L).build(),
                Like.builder().id(3).userId(3L).build()
        );

        userList = List.of(
                new UserDto(1L, "user1", "user1@gmail.com"),
                new UserDto(2L, "user2", "user2@gmail.com"),
                new UserDto(3L, "user3", "user3@gmail.com")
        );
    }

    @Test
    public void toggleLikePost_SuccessLike() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(userServiceClient.getUser(1L)).thenReturn(userDto);

        likeService.toggleLikePost(new LikePostRequest(1L, 1L));

        ArgumentCaptor<Like> captor = ArgumentCaptor.forClass(Like.class);
        verify(likeRepository).save(captor.capture());
        Like newLike = captor.getValue();

        verify(kafkaProducer).sendLikePostEvent(any(), any());

        Assertions.assertTrue(newLike.getUserId() == userDto.id());
        Assertions.assertTrue(newLike.getPost().getId() == post.getId());
    }

    @Test
    public void toggleLikePost_SuccessUnLike() {
        List<Like> likes = new ArrayList<>();
        Like like = Like.builder().userId(userDto.id()).build();
        likes.add(like);
        post.setLikes(likes);

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(userServiceClient.getUser(1L)).thenReturn(userDto);

        likeService.toggleLikePost(new LikePostRequest(1L, 1L));

        ArgumentCaptor<Like> likeCaptor = ArgumentCaptor.forClass(Like.class);
        verify(likeRepository).delete(likeCaptor.capture());

        Assertions.assertEquals(likeCaptor.getValue().getId(), like.getId());
    }

    @Test
    public void toggleLikeComment_SuccessLike() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(userServiceClient.getUser(1L)).thenReturn(userDto);

        likeService.toggleLikeComment(new LikeCommentRequest(1L, 1L));

        ArgumentCaptor<Like> captor = ArgumentCaptor.forClass(Like.class);
        verify(likeRepository).save(captor.capture());
        Like newLike = captor.getValue();

        Assertions.assertTrue(newLike.getUserId() == userDto.id());
        Assertions.assertTrue(newLike.getComment().getId() == comment.getId());
    }

    @Test
    public void toggleLikeComment_SuccessUnLike() {
        Set<Like> likes = new HashSet<>();
        Like like = Like.builder().userId(userDto.id()).build();
        likes.add(like);
        comment.setLikes(likes);

        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(userServiceClient.getUser(1L)).thenReturn(userDto);

        likeService.toggleLikeComment(new LikeCommentRequest(1L, 1L));

        ArgumentCaptor<Like> likeCaptor = ArgumentCaptor.forClass(Like.class);
        verify(likeRepository).delete(likeCaptor.capture());

        Assertions.assertEquals(likeCaptor.getValue().getId(), like.getId());
    }

    @Test
    public void toggleLikePost_WrongPostId() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            likeService.toggleLikePost(new LikePostRequest(1L, 1L));
        });
    }

    @Test
    public void toggleLikeComment_WrongCommentId() {
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            likeService.toggleLikeComment(new LikeCommentRequest(1L, 1L));
        });
    }

    @Test
    public void toggleLikePost_WrongUserId() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(userServiceClient.getUser(1L)).thenReturn(null);

        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            likeService.toggleLikePost(new LikePostRequest(1L, 1L));
        });
    }

    @Test
    public void toggleLikeComment_WrongUserId() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(userServiceClient.getUser(1L)).thenReturn(null);

        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            likeService.toggleLikeComment(new LikeCommentRequest(1L, 1L));
        });
    }


    @Test
    @DisplayName("Test getLikedUsersToPost Success")
    public void getLikedUsersToPost_Success() {
        when(postService.existsById(1L)).thenReturn(true);
        when(likeRepository.findAllByPostId(1L)).thenReturn(likeStream);
        when(userServiceClient.getUsersByIds(List.of(1L, 2L, 3L))).thenReturn(userList);

        List<UserDto> result = likeService.getLikedUsersToPost(1L);

        assertEquals(userList, result);
        assertEquals(3, result.size());
    }

    @Test
    @DisplayName("Test getLikedUsersToPostNotExistsById Error")
    public void getLikedUsersToPost_NotExistsById() {
        when(postService.existsById(2L)).thenReturn(false);

        PostWasNotFoundException exception = assertThrows(PostWasNotFoundException.class, () ->
                likeService.getLikedUsersToPost(2L)
        );

        assertEquals("Post with id 2 does not exist", exception.getMessage());
        verify(postService, times(1)).existsById(2L);
    }

    @Test
    @DisplayName("Test getLikedUsersToPostUserServiceError Error")
    public void getLikedUsersToPost_UserServiceError() {
        when(postService.existsById(1L)).thenReturn(true);
        when(likeRepository.findAllByPostId(1L)).thenReturn(likeStream);
        when(userServiceClient.getUsersByIds(List.of(1L, 2L, 3L))).thenThrow(new RuntimeException());

        UserServiceConnectException exception = assertThrows(UserServiceConnectException.class, () ->
                likeService.getLikedUsersToPost(1L)
        );

        assertEquals("Failed users service", exception.getMessage());
        verify(userServiceClient, times(1)).getUsersByIds(List.of(1L, 2L, 3L));
    }

    @Test
    @DisplayName("Test getLikedUsersToComment Success")
    public void getLikedUsersToComment_Success() {
        when(commentService.existsById(1L)).thenReturn(true);
        when(likeRepository.findAllByCommentId(1L)).thenReturn(likeStream);
        when(userServiceClient.getUsersByIds(List.of(1L, 2L, 3L))).thenReturn(userList);

        List<UserDto> result = likeService.getLikedUsersToComment(1L);

        assertEquals(userList, result);
        assertEquals(3, result.size());
    }

    @Test
    @DisplayName("Test getLikedUsersToCommentNotExistsById Error")
    public void getLikedUsersToComment_NotExistsById() {
        when(commentService.existsById(2L)).thenReturn(false);

        CommentWasNotFoundException exception = assertThrows(CommentWasNotFoundException.class, () ->
                likeService.getLikedUsersToComment(2L)
        );

        assertEquals("Comment with id 2 does not exist", exception.getMessage());
        verify(commentService, times(1)).existsById(2L);
    }

    @Test
    @DisplayName("Test getLikedUsersToCommentUserServiceError Error")
    public void getLikedUsersToComment_UserServiceError() {
        when(commentService.existsById(1L)).thenReturn(true);
        when(likeRepository.findAllByCommentId(1L)).thenReturn(likeStream);
        when(userServiceClient.getUsersByIds(List.of(1L, 2L, 3L))).thenThrow(new RuntimeException());

        UserServiceConnectException exception = assertThrows(UserServiceConnectException.class, () ->
                likeService.getLikedUsersToComment(1L)
        );

        assertEquals("Failed users service", exception.getMessage());
        verify(userServiceClient, times(1)).getUsersByIds(List.of(1L, 2L, 3L));
    }


}
