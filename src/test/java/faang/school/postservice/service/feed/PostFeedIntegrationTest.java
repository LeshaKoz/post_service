package faang.school.postservice.service.feed;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostCreateRequestDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.dto.project.ProjectResponseDto;
import faang.school.postservice.dto.user.SubscriptionUserDto;
import faang.school.postservice.dto.user.UserResponseDto;
import faang.school.postservice.mapper.PostMapperImpl;
import faang.school.postservice.message.event.UsersBanPublisher;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.RedisFeedRepository;
import faang.school.postservice.repository.RedisPostRepository;
import faang.school.postservice.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
@SpringBootTest
@DirtiesContext
class PostFeedIntegrationTest {

    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private RedisPostRepository redisPostRepository;

    @Autowired
    private RedisFeedRepository redisFeedRepository;

    @Autowired
    private FeedService feedService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private FeedEventService feedEventService;

    @MockBean
    private UserServiceClient userServiceClient;

    @MockBean
    ProjectServiceClient projectServiceClient;

    @Spy
    private PostMapperImpl postMapper;

    @Autowired
    private UsersBanPublisher usersBanPublisher;

    @Autowired
    private Executor taskExecutor;

    private final Long authorId = 1L;
    private final Long followerId = 2L;
    private final List<Long> followers = List.of(followerId, 3L, 4L);
    private final Long projectId = 5L;
    private final UserResponseDto authorDto = UserResponseDto.builder().id(authorId).username("test_user 1").build();
    private final SubscriptionUserDto followerDto2 = SubscriptionUserDto.builder()
            .id(followerId).username("test_user 2").build();
    private final SubscriptionUserDto followerDto3 = SubscriptionUserDto.builder()
            .id(3L).username("test_user 3").build();
    private final SubscriptionUserDto followerDto4 = SubscriptionUserDto.builder()
            .id(4L).username("test_user 4").build();
    private final List<SubscriptionUserDto> followersDtos = List.of(followerDto2, followerDto3, followerDto4);
    private final ProjectResponseDto projectDto = ProjectResponseDto.builder().id(projectId).build();
    private Long postId;

    @BeforeEach
    void setUp() {
        postRepository.deleteAll();
        redisTemplate.getConnectionFactory().getConnection().flushAll();

        when(userServiceClient.getFollowers(authorId)).thenReturn(followersDtos);
        when(userServiceClient.getUser(authorId)).thenReturn(authorDto);

        when(projectServiceClient.getProject(projectId)).thenReturn(projectDto);
    }

    @Test
    void testNewPostAddToFeedSuccessful() {
        // 1. Create post draft
        PostCreateRequestDto createDto = PostCreateRequestDto.builder()
                .authorId(authorId)
                .content("Test content")
                .build();

        PostResponseDto draft = postService.createPostDraft(createDto);
        postId = draft.id();

        // 2. Publish post
        PostResponseDto publishedPost = postService.publishPostDraft(postId);

        // 3. Verify DB
        Optional<Post> postFromDb = postRepository.findById(postId);
        assertTrue(postFromDb.isPresent());
        assertEquals(authorId, postFromDb.get().getAuthorId());
        assertNotNull(postFromDb.get().getPublishedAt());

        // 5. Verify Redis Post cache
        Optional<PostResponseDto> postFromRedis = redisPostRepository.getPost(postId);
        assertTrue(postFromRedis.isPresent());
        assertEquals(postId, postFromRedis.get().id());
        assertEquals(authorId, postFromRedis.get().authorId());

        sleep(5);
        // 6. Verify Redis Feed
        List<Long> postIds = redisFeedRepository.getPostIds(followerId, null, 10);
        assertNotNull(postIds);
        assertTrue(postIds.contains(postId));
    }

    @Test
    void testRedisPostCacheSuccessful() {
        PostResponseDto postDto = PostResponseDto.builder()
                .id(1L)
                .authorId(authorId)
                .content("Test content")
                .publishedAt(LocalDateTime.now())
                .build();

        redisPostRepository.addNewPost(postDto);
        Optional<PostResponseDto> fromRedis = redisPostRepository.getPost(1L);

        assertTrue(fromRedis.isPresent());
        assertEquals(postDto.id(), fromRedis.get().id());
        assertEquals(postDto.content(), fromRedis.get().content());
    }

    private static void sleep(long second) {
        try {
            Thread.sleep(second * 1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}