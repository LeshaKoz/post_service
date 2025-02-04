package faang.school.postservice.service.like;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.like.comment.LikeCommentDto;
import faang.school.postservice.dto.like.comment.LikeCommentDtoResponse;
import faang.school.postservice.dto.like.post.LikePostDto;
import faang.school.postservice.dto.like.post.LikePostDtoResponse;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Like;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.validator.LikeValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class LikeServiceImplTest {

    @InjectMocks
    private LikeServiceImpl likeService;

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private LikeMapper likeMapper;

    @Mock
    private UserContext userContext;

    @Mock
    private LikeValidator likeValidator;

    private LikePostDto likePostDto;
    private LikeCommentDto likeCommentDto;
    private Like like;
    private LikePostDtoResponse likePostDtoResponse;
    private LikeCommentDtoResponse likeCommentDtoResponse;

    @BeforeEach
    void setUp() {

        likePostDto = new LikePostDto(1L, 2L, 3L);
        likeCommentDto = new LikeCommentDto(1L, 2L, 3L, 4L);
        like = new Like();
        like.setId(1L);
        like.setUserId(2L);
        likePostDtoResponse = new LikePostDtoResponse(1L, 2L, 3L);
        likeCommentDtoResponse = new LikeCommentDtoResponse(1L, 2L, 3L, 4L);
    }

    @Test
    void testCreateLikeForPostSuccess() {

        when(likeMapper.toLike(likePostDto)).thenReturn(like);
        when(likeRepository.save(like)).thenReturn(like);
        when(likeMapper.toLikePostDtoResponse(like)).thenReturn(likePostDtoResponse);

        LikePostDtoResponse response = likeService.createLikeForPost(likePostDto);

        assertNotNull(response);
        assertEquals(likePostDtoResponse, response);
        verify(likeValidator).validateUserExists(likePostDto.userId());
        verify(likeValidator).validatePostExists(likePostDto.postId());
        verify(likeValidator).validatePostLiked(likePostDto.postId(), likePostDto.userId());
        verify(likeRepository).save(like);
    }

    @Test
    void testCreateLikeForCommentSuccess() {

        when(likeMapper.toLike(likeCommentDto)).thenReturn(like);
        when(likeRepository.save(like)).thenReturn(like);
        when(likeMapper.toLikeCommentDtoResponse(like)).thenReturn(likeCommentDtoResponse);

        LikeCommentDtoResponse response = likeService.createLikeForComment(likeCommentDto);

        assertNotNull(response);
        assertEquals(likeCommentDtoResponse, response);
        verify(likeValidator).validateUserExists(likeCommentDto.userId());
        verify(likeValidator).validateCommentExists(likeCommentDto.postId());
        verify(likeValidator).validateCommentLiked(likeCommentDto.postId(), likeCommentDto.userId());
        verify(likeRepository).save(like);
    }

    @Test
    void testDeleteLikeFromPost() {

        long postId = 10L;
        long userId = 20L;
        when(userContext.getUserId()).thenReturn(userId);

        likeService.deleteLikeFromPost(postId);

        verify(likeRepository).deleteByPostIdAndUserId(postId, userId);
        verify(likeValidator).validateUserId(userId);
    }

    @Test
    void testDeleteLikeFromComment() {

        long commentId = 30L;
        long userId = 40L;
        when(userContext.getUserId()).thenReturn(userId);

        likeService.deleteLikeFromComment(commentId);

        verify(likeRepository).deleteByCommentIdAndUserId(commentId, userId);
        verify(likeValidator).validateUserId(userId);
    }
}