package faang.school.postservice.controller;

import faang.school.postservice.controller.like.LikeController;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.service.like.LikeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LikeControllerTest {

    @Mock
    private LikeService likeService;

    @InjectMocks
    private LikeController likeController;

    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userDto = UserDto.builder()
                .id(1L)
                .username("user1")
                .email("user1@example.com")
                .build();
    }

    @Test
    void testGetUsersWhoLikedPost() {
        long postId = 1L;
        when(likeService.getUsersWhoLikedPost(postId)).thenReturn(List.of(userDto));

        List<UserDto> result = likeController.getUsersWhoLikedPost(postId);

        assertEquals(1, result.size());
        assertEquals(userDto, result.get(0));

        verify(likeService, times(1)).getUsersWhoLikedPost(postId);
    }

    @Test
    void testGetUsersWhoLikedComment() {
        long commentId = 1L;
        when(likeService.getUsersWhoLikedComment(commentId)).thenReturn(List.of(userDto));

        List<UserDto> result = likeController.getUsersWhoLikedComment(commentId);

        assertEquals(1, result.size());
        assertEquals(userDto, result.get(0));

        verify(likeService, times(1)).getUsersWhoLikedComment(commentId);
    }

    @Test
    void testGetUsersWhoLikedPost_EmptyList() {
        long postId = 1L;
        when(likeService.getUsersWhoLikedPost(postId)).thenReturn(List.of());

        List<UserDto> result = likeController.getUsersWhoLikedPost(postId);

        assertEquals(0, result.size());

        verify(likeService, times(1)).getUsersWhoLikedPost(postId);
    }

    @Test
    void testGetUsersWhoLikedComment_EmptyList() {
        long commentId = 1L;
        when(likeService.getUsersWhoLikedComment(commentId)).thenReturn(List.of());

        List<UserDto> result = likeController.getUsersWhoLikedComment(commentId);

        assertEquals(0, result.size());

        verify(likeService, times(1)).getUsersWhoLikedComment(commentId);
    }
}