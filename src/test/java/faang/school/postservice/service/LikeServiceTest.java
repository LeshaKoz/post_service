package faang.school.postservice.service;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.exception.ConcurrentLikeException;
import faang.school.postservice.exception.DuplicateEntityException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LikeServiceTest {

    private final Long firstId = 1L;

    @InjectMocks
    private LikeService likeService;

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserContext userContext;

    @BeforeEach
    public void setUp() {
        likeService = new LikeService(likeRepository, postRepository, commentRepository, userContext);
    }

    @Test
    @DisplayName("Test negative put like on post when user not found")
    public void testNegativeFirstPutLikeOnPost() {
        assertThrows(EntityNotFoundException.class, () -> likeService.putLikeOnPost(firstId));
    }

    @Test
    @DisplayName("Test negative put like on post when user already put like on this post")
    public void testNegativeSecondPutLikeOnPost() {
        includeSecondNegativeTestLikeOnPost();
        Optional<Like> like = Optional.ofNullable(createLike(firstId, null, null));
        when(userContext.getUserId()).thenReturn(firstId);
        when(likeRepository.findByPostIdAndUserId(firstId, firstId)).thenReturn(like);

        assertThrows(DuplicateEntityException.class, () -> likeService.putLikeOnPost(firstId));
    }

    @Test
    @DisplayName("Test negative put like on post when user already put like on comment of this post")
    public void testNegativeThirdPutLikeOnPost() {
        includeSecondNegativeTestLikeOnPost();
        Like like = createLike(firstId, null, null);
        Comment comment = createComment(firstId, List.of(like), null);
        Post post = createPost(firstId, List.of(comment), Collections.emptyList());
        when(userContext.getUserId()).thenReturn(firstId);
        when(likeRepository.findByPostIdAndUserId(firstId, firstId)).thenReturn(Optional.empty());
        when(postRepository.findById(firstId)).thenReturn(Optional.of(post));

        assertThrows(ConcurrentLikeException.class, () -> likeService.putLikeOnPost(firstId));
    }

    @Test
    public void testPositivePutLikeOnPostSuccessful() {
        includeSecondNegativeTestLikeOnPost();
        Post post = createPost(firstId, Collections.emptyList(), Collections.emptyList());
        Like like = createLike(firstId, post, null);

        when(userContext.getUserId()).thenReturn(firstId);
        when(likeRepository.findByPostIdAndUserId(firstId, firstId)).thenReturn(Optional.empty());

        likeService.putLikeOnPost(firstId);

        verify(likeRepository, times(1)).save(like);
    }

    @Test
    @DisplayName("Test negative remove like at post when user not found")
    public void testNegativeFirstRemoveLikeAtPost() {
        assertThrows(EntityNotFoundException.class, () -> likeService.removeLikeAtPost(firstId));
    }

    @Test
    @DisplayName("Test negative remove like on post when post not found")
    public void testNegativeSecondRemoveLikeAtPost() {
        when(userContext.getUserId()).thenReturn(firstId);
        when(likeRepository.findByPostIdAndUserId(firstId, firstId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> likeService.removeLikeAtPost(firstId));
    }

    @Test
    public void testPositiveRemoveLikeAtPostSuccessful() {
        Like like = createLike(firstId,
                createPost(firstId, Collections.emptyList(), Collections.emptyList()), null);
        when(userContext.getUserId()).thenReturn(firstId);
        when(likeRepository.findByPostIdAndUserId(firstId, firstId)).thenReturn(Optional.ofNullable(like));

        likeService.removeLikeAtPost(firstId);

        verify(likeRepository, times(1)).deleteByPostIdAndUserId(firstId, firstId);
    }

    @Test
    @DisplayName("Test negative put like on comment when user not found")
    public void testNegativeFirstPutLikeOnComment() {
        assertThrows(EntityNotFoundException.class, () -> likeService.putLikeOnComment(firstId));
    }

    @Test
    @DisplayName("Test negative put like on comment when user already put like on this comment")
    public void testNegativeSecondPutLikeOnComment() {
        includeSecondNegativeTestLikeOnComment();
        Optional<Like> like = Optional.ofNullable(createLike(firstId, null, null));
        when(userContext.getUserId()).thenReturn(firstId);
        when(likeRepository.findByCommentIdAndUserId(firstId, firstId)).thenReturn(like);

        assertThrows(DuplicateEntityException.class, () -> likeService.putLikeOnComment(firstId));
    }

    @Test
    @DisplayName("Test negative put like on comment when user already put like on post with this comment")
    public void testNegativeThirdPutLikeOnComment() {
        includeSecondNegativeTestLikeOnComment();
        Like like = createLike(firstId, null, null);
        Post post = createPost(firstId, null, List.of(like));
        Comment comment = createComment(firstId, null, post);
        when(userContext.getUserId()).thenReturn(firstId);
        when(likeRepository.findByCommentIdAndUserId(firstId, firstId)).thenReturn(Optional.empty());
        when(commentRepository.findById(firstId)).thenReturn(Optional.of(comment));

        assertThrows(ConcurrentLikeException.class, () -> likeService.putLikeOnComment(firstId));
    }

    @Test
    public void testPositivePutLikeOnCommentSuccessful() {
        includeSecondNegativeTestLikeOnComment();
        Post post = createPost(firstId, Collections.emptyList(), Collections.emptyList());
        Comment comment = createComment(firstId, Collections.emptyList(), post);
        Like like = createLike(firstId, null, comment);
        when(userContext.getUserId()).thenReturn(firstId);
        when(likeRepository.findByCommentIdAndUserId(firstId, firstId)).thenReturn(Optional.empty());

        likeService.putLikeOnComment(firstId);

        verify(likeRepository, times(1)).save(like);
    }

    @Test
    @DisplayName("Test negative remove like at comment when user not found")
    public void testNegativeFirstRemoveLikeAtComment() {
        assertThrows(EntityNotFoundException.class, () -> likeService.removeLikeAtComment(firstId));
    }

    @Test
    @DisplayName("Test negative remove like at comment when comment not found")
    public void testNegativeSecondRemoveLikeAtComment() {
        when(userContext.getUserId()).thenReturn(firstId);
        when(likeRepository.findByCommentIdAndUserId(firstId, firstId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> likeService.removeLikeAtComment(firstId));
    }

    @Test
    public void testPositiveRemoveLikeAtCommentSuccessful() {
        Like like = createLike(firstId, null, createComment(firstId, null, null));
        when(userContext.getUserId()).thenReturn(firstId);
        when(likeRepository.findByCommentIdAndUserId(firstId, firstId)).thenReturn(Optional.ofNullable(like));

        likeService.removeLikeAtComment(firstId);

        verify(likeRepository, times(1)).deleteByCommentIdAndUserId(firstId, firstId);
    }

    private Post createPost(Long id, List<Comment> comments, List<Like> likes) {
        return Post.builder()
                .id(id)
                .likes(likes)
                .comments(comments)
                .build();
    }

    private Comment createComment(Long id, List<Like> likes, Post post) {
        return Comment.builder()
                .id(id)
                .likes(likes)
                .post(post)
                .build();
    }

    private Like createLike(Long id, Post post, Comment comment) {
        return Like.builder()
                .userId(id)
                .comment(comment)
                .post(post)
                .build();
    }

    private void includeSecondNegativeTestLikeOnPost() {
        Optional<Post> post = Optional.ofNullable(
                createPost(firstId, Collections.emptyList(), Collections.emptyList()));
        when(postRepository.findById(firstId)).thenReturn(post);
    }

    private void includeSecondNegativeTestLikeOnComment() {
        Optional<Comment> comment = Optional.ofNullable(
                createComment(firstId, Collections.emptyList(),
                        createPost(firstId, Collections.emptyList(), Collections.emptyList())));
        when(commentRepository.findById(firstId)).thenReturn(comment);
    }
}
