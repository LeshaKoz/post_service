package faang.school.postservice.service;

import faang.school.postservice.dto.LikeViewDto;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.validation.LikeValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class LikeServiceTest {
    @Mock
    private LikeRepository likeRepository;
    @Mock
    private LikeMapper likeMapper;
    @Mock
    private PostService postService;
    @Mock
    private LikeValidator likeValidator;
    @Mock
    private CommentService commentService;

    @InjectMocks
    private LikeService likeService;

    private final Like likeEntity = new Like();
    private final Post postEntity = new Post();
    private final LikeViewDto likeViewDto = new LikeViewDto();
    private final Comment commentEntity = new Comment();
    private final long postId = 1L;
    private final long commentId = 1L;
    private final long userId = 1L;

    @BeforeEach
    public void setUp() {
        likeViewDto.setUserId(userId);
    }

    @Test
    @DisplayName("likePost: позитивный сценарий")
    public void givenValidPostAndUserWhenLikePostThenReturnLikeViewDto() {
        likeViewDto.setPostId(postId);
        Mockito.when(postService.getPostEntity(postId)).thenReturn(postEntity);
        Mockito.when(likeRepository.save(Mockito.any(Like.class))).thenReturn(likeEntity);
        Mockito.when(likeMapper.toDto(likeEntity)).thenReturn(likeViewDto);

        LikeViewDto result = likeService.likePost(postId, userId);
        Assertions.assertNotNull(result);

        Mockito.verify(likeValidator).validateForAddingPostLike(postId, userId);
        Mockito.verify(postService).getPostEntity(postId);
        Mockito.verify(likeRepository).save(Mockito.any(Like.class));
        Mockito.verify(likeMapper).toDto(likeEntity);
    }

    @Test
    @DisplayName("unlikePost: позитивный сценарий")
    public void givenValidPostAndUserWhenUnlikePostThenLikeIsDeleted() {
        likeService.unlikePost(postId, userId);

        Mockito.verify(likeValidator, Mockito.times(1))
                .validateForRemovingPostLike(postId, userId);
        Mockito.verify(likeRepository, Mockito.times(1))
                .deleteByPostIdAndUserId(postId, userId);
    }

    @Test
    @DisplayName("likeComment: позитивный сценарий")
    public void givenValidPostAndUserWhenLikeCommentThenReturnLikeViewDto() {
        likeViewDto.setCommentId(commentId);
        Mockito.when(commentService.getCommentEntity(commentId)).thenReturn(commentEntity);
        Mockito.when(likeRepository.save(Mockito.any(Like.class))).thenReturn(likeEntity);
        Mockito.when(likeMapper.toDto(likeEntity)).thenReturn(likeViewDto);

        LikeViewDto result = likeService.likeComment(commentId, userId);
        Assertions.assertNotNull(result);

        Mockito.verify(likeValidator).validateForAddingCommentLike(commentId, userId);
        Mockito.verify(commentService).getCommentEntity(commentId);
        Mockito.verify(likeRepository).save(Mockito.any(Like.class));
        Mockito.verify(likeMapper).toDto(likeEntity);
    }

    @Test
    @DisplayName("unlikeComment: позитивный сценарий")
    public void givenValidPostAndUserWhenUnlikeCommentThenLikeIsDeleted() {
        likeService.unlikeComment(commentId, userId);

        Mockito.verify(likeValidator, Mockito.times(1))
                .validateForRemovingCommentLike(commentId, userId);
        Mockito.verify(likeRepository, Mockito.times(1))
                .deleteByCommentIdAndUserId(commentId, userId);
    }
}
