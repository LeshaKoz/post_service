package faang.school.postservice.controller;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.service.LikeService;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class LikeControllerTest {

    @Mock
    private LikeService likeService;

    @Mock
    private LikeMapper likeMapper;

    @Mock
    private UserContext userContext;

    @InjectMocks
    private LikeDto likeDtoInvalidPostId;
    private LikeDto likeDtoInvalidCommentId;
    private LikeDto likeDtoValid;
    Long positiveUserId;

    @BeforeEach
    void setUp() {
        likeDtoInvalidPostId = new LikeDto(1L, 1L, 1L, -1L);
        likeDtoInvalidCommentId = new LikeDto(1L, 1L, -1L, 1L);
        likeDtoValid = new LikeDto(1L, 1L, 1L, 1L);
        positiveUserId = 1L;
    }

    @InjectMocks
    private LikeController likeController;

    @Test
    public void testLikePostWithInvalidLikeDto() {
        Assert.assertThrows(DataValidationException.class,
                () -> likeController.likePost(likeDtoInvalidPostId));
    }

    @Test
    public void testLikePostWithValidLikeDto() {
        Mockito.when(userContext.getUserId()).thenReturn(positiveUserId);
        likeController.likePost(likeDtoValid);
        Mockito.verify(likeService, Mockito.times(1)).likePost(Mockito.any());
    }

    @Test
    public void testLikeCommentWithInvalidLikeDto() {
        Assert.assertThrows(DataValidationException.class,
                () -> likeController.likeComment(likeDtoInvalidCommentId));
    }

    @Test
    public void testLikeCommentWithValidLikeDto() {
        Mockito.when(userContext.getUserId()).thenReturn(positiveUserId);
        likeController.likeComment(likeDtoValid);
        Mockito.verify(likeService, Mockito.times(1)).likeComment(Mockito.any());
    }
}
