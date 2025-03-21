package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.validator.CommentValidator;
import faang.school.postservice.validator.PostValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LikeServiceTest {

    private static final Long POST_ID = 1L;
    private static final Long USER_ID = 2L;
    private static final Long COMMENT_ID = 3L;

    private Post post;
    private Comment comment;
    private UserDto userDto;

    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private PostValidator postValidator;
    @Mock
    private CommentValidator commentValidator;
    @InjectMocks
    private LikeService likeService;

    @BeforeEach
    public void setUp() {
        post = Post.builder()
                .likes(List.of(new Like()))
                .build();
        comment = Comment.builder()
                .likes(List.of(new Like()))
                .build();
        userDto = new UserDto(USER_ID, "Join", "join@mail.ru");
    }

    @Test
    public void testGetAllUsersWhoLikedPostSuccessful() {
        when(postValidator.getPostById(anyLong())).thenReturn(post);
        when(userServiceClient.getUsersByIds(anyList())).thenReturn(List.of(userDto));

        List<UserDto> resultDto = likeService.getAllUsersWhoLikedPost(POST_ID);

        verify(postValidator, times(1)).getPostById(POST_ID);
        verify(userServiceClient, times(1)).getUsersByIds(anyList());
        assertEquals(resultDto.get(0).id(), userDto.id());
        assertEquals(resultDto.get(0).username(), userDto.username());
        assertEquals(resultDto.get(0).email(), userDto.email());
    }

    @Test
    public void testGetAllUsersWhoLikedPostEmptyList() {
        when(postValidator.getPostById(anyLong())).thenReturn(post);
        when(userServiceClient.getUsersByIds(anyList())).thenReturn(Collections.emptyList());

        List<UserDto> result = likeService.getAllUsersWhoLikedPost(POST_ID);

        verify(postValidator, times(1)).getPostById(POST_ID);
        verify(userServiceClient, times(1)).getUsersByIds(anyList());
        assertEquals(0, result.size());
    }

    @Test
    public void testGetAllUsersPostNotFound() {
        validatePostNotFound();
    }

    @Test
    public void testGetAllUsersWhoLikedCommentSuccessful() {
        when(commentValidator.getCommentById(anyLong())).thenReturn(comment);
        when(userServiceClient.getUsersByIds(anyList())).thenReturn(List.of(userDto));

        List<UserDto> resultDto = likeService.getAllUsersWhoLikedComment(COMMENT_ID);

        verify(commentValidator, times(1)).getCommentById(COMMENT_ID);
        verify(userServiceClient, times(1)).getUsersByIds(anyList());
        assertEquals(userDto.id(), resultDto.get(0).id());
        assertEquals(userDto.username(), resultDto.get(0).username());
        assertEquals(userDto.email(), resultDto.get(0).email());
    }

    @Test
    public void testGetAllUsersWhoLikedCommentEmptyList() {
        when(commentValidator.getCommentById(anyLong())).thenReturn(comment);
        when(userServiceClient.getUsersByIds(anyList())).thenReturn(Collections.emptyList());

        List<UserDto> result = likeService.getAllUsersWhoLikedComment(COMMENT_ID);

        verify(commentValidator, times(1)).getCommentById(COMMENT_ID);
        verify(userServiceClient, times(1)).getUsersByIds(anyList());
        assertEquals(0, result.size());
    }

    @Test
    public void testGetAllUsersCommentNotFound() {
        validateCommentNotFound();
    }

    private void validatePostNotFound() {
        String errorMessage = "Post not found";
        when(postValidator.getPostById(POST_ID))
                .thenThrow(new EntityNotFoundException(errorMessage));

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> postValidator.getPostById(POST_ID));

        assertEquals(errorMessage, exception.getMessage());
        verify(userServiceClient, never()).getUsersByIds(anyList());
    }

    private void validateCommentNotFound() {
        String errorMessage = "Comment not found";
        when(commentValidator.getCommentById(COMMENT_ID))
                .thenThrow(new EntityNotFoundException(errorMessage));

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> commentValidator.getCommentById(COMMENT_ID));

        assertEquals(errorMessage, exception.getMessage());
        verify(userServiceClient, never()).getUsersByIds(anyList());
    }
}
