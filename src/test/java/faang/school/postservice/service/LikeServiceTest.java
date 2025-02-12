package faang.school.postservice.service;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.BusinessException;
import faang.school.postservice.mapper.like.LikeMapperImpl;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.LikeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {
    private static final Long LIKE_ID = 1L;
    private static final Long USER_ID = 1L;
    private static final Long COMMENT_ID = 1L;
    private static final Long POST_ID = 1L;

    @InjectMocks
    private LikeService likeService;

    @Spy
    private LikeMapperImpl likeMapper;

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private PostService postService;

    @Mock
    private CommentService commentService;

    @Mock
    private UserService userService;

    private Like like;
    private UserDto userDto;
    private Comment comment;
    private Post post;

    @BeforeEach
    void setUp() {
        like = Like.builder()
                .id(LIKE_ID)
                .userId(USER_ID)
                .build();

        userDto = UserDto.builder()
                .id(USER_ID)
                .build();

        post = Post.builder()
                .id(COMMENT_ID)
                .authorId(USER_ID)
                .build();

        comment = Comment.builder()
                .id(COMMENT_ID)
                .authorId(USER_ID)
                .build();
    }

    @Test
    void shouldThrowNotFoundLike() {
        Mockito.when(likeRepository.findById(LIKE_ID)).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class, () -> likeService.findById(LIKE_ID));

        Mockito.verify(likeRepository).findById(LIKE_ID);
    }

    @Test
    void shouldFoundLike() {
        Mockito.when(likeRepository.findById(LIKE_ID)).thenReturn(Optional.of(like));

        Assertions.assertEquals(like, likeService.findById(LIKE_ID));

        Mockito.verify(likeRepository).findById(LIKE_ID);
    }

    @Test
    void shouldThrowPostHaveLike() {
        like.setPost(post);
        LikeDto dto = likeMapper.toPostDto(like);

        Mockito.when(userService.getUserByContext()).thenReturn(userDto);
        Mockito.when(likeRepository.findByPostIdAndUserId(POST_ID, USER_ID)).thenReturn(Optional.of(like));

        Assertions.assertThrows(BusinessException.class, () -> likeService.userLikeThePost(dto));

        Mockito.verify(userService).getUserByContext();
        Mockito.verify(likeRepository).findByPostIdAndUserId(POST_ID, USER_ID);
    }

    @Test
    void shouldThrowCommentHaveLike() {
        like.setComment(comment);
        LikeDto dto = likeMapper.toCommentDto(like);

        Mockito.when(userService.getUserByContext()).thenReturn(userDto);
        Mockito.when(likeRepository.findByCommentIdAndUserId(POST_ID, USER_ID)).thenReturn(Optional.of(like));

        Assertions.assertThrows(BusinessException.class, () -> likeService.userLikeTheComment(dto));

        Mockito.verify(userService).getUserByContext();
        Mockito.verify(likeRepository).findByCommentIdAndUserId(POST_ID, USER_ID);
    }

    @Test
    void shouldReturnLikePost() {
        like.setPost(post);
        LikeDto likeDto = likeMapper.toPostDto(like);

        Mockito.when(userService.getUserByContext()).thenReturn(userDto);
        Mockito.when(likeRepository.findByPostIdAndUserId(POST_ID, USER_ID)).thenReturn(Optional.empty());
        Mockito.when(postService.findById(POST_ID)).thenReturn(post);

        var savedLike = new Like();
        savedLike.setId(LIKE_ID);
        savedLike.setUserId(USER_ID);
        savedLike.setPost(post);
        Mockito.when(likeRepository.save(Mockito.any(Like.class))).thenReturn(savedLike);

        LikeDto result = likeService.userLikeThePost(likeDto);

        Assertions.assertNotNull(result);
        Mockito.verify(likeRepository).save(Mockito.any(Like.class));
        Mockito.verify(postService).findById(POST_ID);
        Mockito.verify(likeRepository).findByPostIdAndUserId(POST_ID, USER_ID);
    }

    @Test
    void shouldReturnLikeComment() {
        like.setComment(comment);
        LikeDto likeDto = likeMapper.toCommentDto(like);

        Mockito.when(userService.getUserByContext()).thenReturn(userDto);
        Mockito.when(likeRepository.findByCommentIdAndUserId(COMMENT_ID, USER_ID)).thenReturn(Optional.empty());
        Mockito.when(commentService.getCommentById(COMMENT_ID)).thenReturn(comment);

        var savedLike = new Like();
        savedLike.setId(LIKE_ID);
        savedLike.setUserId(USER_ID);
        savedLike.setComment(comment);
        Mockito.when(likeRepository.save(Mockito.any(Like.class))).thenReturn(savedLike);

        LikeDto result = likeService.userLikeTheComment(likeDto);

        Assertions.assertNotNull(result);
        Mockito.verify(likeRepository).save(Mockito.any(Like.class));
        Mockito.verify(commentService).getCommentById(COMMENT_ID);
        Mockito.verify(likeRepository).findByCommentIdAndUserId(COMMENT_ID, USER_ID);
    }

    @Test
    void shouldThrowUserCanNotDeleteLikeOnPost() {
        like.setUserId(2L);
        LikeDto likeDto = likeMapper.toPostDto(like);

        Mockito.when(userService.getUserByContext()).thenReturn(userDto);
        Mockito.when(likeRepository.findById(LIKE_ID)).thenReturn(Optional.of(like));

        Assertions.assertThrows(BusinessException.class, () -> likeService.removeLikePost(LIKE_ID, likeDto));

        Mockito.verify(userService).getUserByContext();
        Mockito.verify(likeRepository).findById(LIKE_ID);
    }

    @Test
    void shouldThrowUserCanNotDeleteLikeOnComment() {
        like.setUserId(2L);
        LikeDto likeDto = likeMapper.toCommentDto(like);

        Mockito.when(userService.getUserByContext()).thenReturn(userDto);
        Mockito.when(likeRepository.findById(LIKE_ID)).thenReturn(Optional.of(like));

        Assertions.assertThrows(BusinessException.class, () -> likeService.removeLikeComment(LIKE_ID, likeDto));

        Mockito.verify(userService).getUserByContext();
        Mockito.verify(likeRepository).findById(LIKE_ID);
    }

    @Test
    void shouldDeleteLikeOnComment() {
        like.setComment(comment);

        LikeDto likeDto = likeMapper.toCommentDto(like);

        Mockito.when(userService.getUserByContext()).thenReturn(userDto);
        Mockito.when(likeRepository.findById(LIKE_ID)).thenReturn(Optional.of(like));
        Mockito.doNothing().when(likeRepository).deleteByCommentIdAndUserId(COMMENT_ID, USER_ID);

        likeService.removeLikeComment(LIKE_ID, likeDto);

        Mockito.verify(userService).getUserByContext();
        Mockito.verify(likeRepository).findById(LIKE_ID);
        Mockito.verify(likeRepository).deleteByCommentIdAndUserId(COMMENT_ID, USER_ID);
    }

    @Test
    void shouldDeleteLikeOnPost() {
        like.setPost(post);

        LikeDto likeDto = likeMapper.toPostDto(like);

        Mockito.when(userService.getUserByContext()).thenReturn(userDto);
        Mockito.when(likeRepository.findById(LIKE_ID)).thenReturn(Optional.of(like));
        Mockito.doNothing().when(likeRepository).deleteByCommentIdAndUserId(POST_ID, USER_ID);

        likeService.removeLikeComment(LIKE_ID, likeDto);

        Mockito.verify(userService).getUserByContext();
        Mockito.verify(likeRepository).findById(LIKE_ID);
        Mockito.verify(likeRepository).deleteByCommentIdAndUserId(POST_ID, USER_ID);
    }

}