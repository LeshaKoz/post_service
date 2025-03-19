package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.like.LikeMapperImpl;
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
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class LikeServiceTest {

    @InjectMocks
    private LikeService likeService;

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @Spy
    private LikeMapperImpl likeMapper;

    @BeforeEach
    public void setUp() {
        likeService = new LikeService(likeRepository, postRepository, commentRepository, userServiceClient, likeMapper);
    }

    @Test
    @DisplayName("Test negative put like on post when user not found")
    public void testNegativeFirstPutLikeOnPost() {

    }

    @Test
    @DisplayName("Test negative put like on post when post not found")
    public void testNegativeSecondPutLikeOnPost() {

    }

    @Test
    @DisplayName("Test negative put like on post when user already put like on this post")
    public void testNegativeThirdPutLikeOnPost() {

    }

    @Test
    @DisplayName("Test negative put like on post when user already put like on comment of this post")
    public void testNegativeFourthPutLikeOnPost() {

    }

    @Test
    public void testPositivePutLikeOnPostSuccessful() {

    }

    @Test
    @DisplayName("Test negative remove like at post when user not found")
    public void testNegativeFirstRemoveLikeAtPost() {

    }

    @Test
    @DisplayName("Test negative remove like on post when post not found")
    public void testNegativeSecondRemoveLikeAtPost() {

    }

    @Test
    public void testPositiveRemoveLikeAtPostSuccessful() {

    }

    @Test
    @DisplayName("Test negative put like on comment when user not found")
    public void testNegativeFirstPutLikeOnComment() {

    }

    @Test
    @DisplayName("Test negative put like on comment when comment not found")
    public void testNegativeSecondPutLikeOnComment() {

    }

    @Test
    @DisplayName("Test negative put like on comment when user already put like on this comment")
    public void testNegativeThirdPutLikeOnComment() {

    }

    @Test
    @DisplayName("Test negative put like on comment when user already put like on post with this comment")
    public void testNegativeFourthPutLikeOnComment() {

    }

    @Test
    public void testPositivePutLikeOnCommentSuccessful() {

    }

    @Test
    @DisplayName("Test negative remove like at comment when user not found")
    public void testNegativeFirstRemoveLikeAtComment() {

    }

    @Test
    @DisplayName("Test negative remove like at comment when comment not found")
    public void testNegativeSecondRemoveLikeAtComment() {

    }

    @Test
    public void testPositiveRemoveLikeAtCommentSuccessful() {

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

    private UserDto createUser(Long id) {
        return UserDto.builder()
                .id(id)
                .build();
    }
}
