package faang.school.postservice.service.feed;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostCreateRequestDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.dto.project.ProjectResponseDto;
import faang.school.postservice.dto.user.SubscriptionUserDto;
import faang.school.postservice.dto.user.UserResponseDto;
import faang.school.postservice.mapper.PostMapperImpl;
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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
@SpringBootTest
/* @EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
@TestPropertySource(properties = {
        "spring.kafka.bootstrap-servers=localhost:9092",
        "spring.data.kafka.topics.post.name=test-post-topic",
        "spring.data.kafka.consumer.groups.post=test-group",
        "feed.kafka.subscribersBatchSize=10",
        "feed.kafka.postTopic=test-post-topic",
        "spring.data.redis.host=localhost",
        "spring.data.redis.port=6379",
        "redis.ttl=3600",
        "redis.feed-max-size=1000",
        "redis.page-size=10"
}) */
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

    //@Autowired
    //private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Spy
    private PostMapperImpl postMapper;

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
    void testNewPostAddToFeed() {
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

        // 7. Verify FeedService
        //when(userServiceClient.getFolloweeIdsByFollowerId(FOLLOWER_ID)).thenReturn(Collections.singletonList(AUTHOR_ID));
        //List<FeedPostDto> feed = feedService.getFeed(FOLLOWER_ID, LocalDateTime.now());
        //assertFalse(feed.isEmpty());
        //assertEquals(postId, feed.get(0).getPostDto().id());
        //assertEquals(AUTHOR_ID, feed.get(0).getAuthor().id());

    }

    @Test
    void testRedisCache() {

    }

    @Test
    void testPostCreatedAndAppearsInFeed() throws Exception {
        // 1. Create post draft
        /*PostCreateRequestDto createDto = PostCreateRequestDto.builder()
                .authorId(AUTHOR_ID)
                .content("Test content")
                .build();

        PostResponseDto draft = postService.createPostDraft(createDto);
        postId = draft.id();

        // 2. Publish post
        PostResponseDto publishedPost = postService.publishPostDraft(postId);

        // 3. Verify DB
        Optional<Post> postFromDb = postRepository.findById(postId);
        assertTrue(postFromDb.isPresent());
        assertEquals(AUTHOR_ID, postFromDb.get().getAuthorId());
        assertNotNull(postFromDb.get().getPublishedAt());

        // 4. Verify Kafka event
        //ConsumerRecord<String, FeedPostEvent> record = getKafkaRecord();
        //assertNotNull(record);

        /*FeedPostEvent event = record.value();
        assertEquals(postId, event.getPostId());
        assertEquals(AUTHOR_ID, event.getAuthorId());
        assertEquals(FOLLOWERS, event.getSubscribersIds());
        assertEquals(publishedPost.publishedAt(), event.getPublishedAt());
* /
        // 5. Verify Redis Post cache
        Optional<PostResponseDto> postFromRedis = redisPostRepository.getPost(postId);
        assertTrue(postFromRedis.isPresent());
        assertEquals(postId, postFromRedis.get().id());
        assertEquals(AUTHOR_ID, postFromRedis.get().authorId());

        // 6. Verify Redis Feed
        List<Long> postIds = redisFeedRepository.getPostIds(FOLLOWER_ID, null, 10);
        assertTrue(postIds.contains(postId));

        // 7. Verify FeedService
        List<FeedPostDto> feed = feedService.getFeed(FOLLOWER_ID, null);
        assertFalse(feed.isEmpty());
        assertEquals(postId, feed.get(0).getPostDto().id());
        assertEquals(AUTHOR_ID, feed.get(0).getAuthor().id());
        */
    }
/*
    private ConsumerRecord<String, FeedPostEvent> getKafkaRecord() {
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("test-group", "true", embeddedKafkaBroker);
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        consumerProps.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        Consumer<String, FeedPostEvent> consumer = new DefaultKafkaConsumerFactory<String, FeedPostEvent>(
                consumerProps,
                new StringDeserializer(),
                new JsonDeserializer<>(FeedPostEvent.class))
                .createConsumer();

        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumer, "test-post-topic");

        return KafkaTestUtils.getSingleRecord(consumer, "test-post-topic", Duration.ofSeconds(5));
    }
*/
    @Test
    void testRedisPostCache() {
        /* PostResponseDto postDto = PostResponseDto.builder()
                .id(1L)
                .authorId(AUTHOR_ID)
                .content("Test content")
                .publishedAt(LocalDateTime.now())
                .build();

        redisPostRepository.addNewPost(postDto);
        Optional<PostResponseDto> fromRedis = redisPostRepository.getPost(1L);

        assertTrue(fromRedis.isPresent());
        assertEquals(postDto.id(), fromRedis.get().id());
        assertEquals(postDto.content(), fromRedis.get().content());

         */
    }

    @Test
    void testFeedService() {
        /*
        // Setup test data
        Long postId = 1L;
        LocalDateTime publishedAt = LocalDateTime.now();

        // Add post to Redis cache
        PostResponseDto postDto = PostResponseDto.builder()
                .id(postId)
                .authorId(AUTHOR_ID)
                .content("Test content")
                .publishedAt(publishedAt)
                .build();
        redisPostRepository.addNewPost(postDto);

        // Add post to follower's feed
        double score = publishedAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        redisFeedRepository.addPost(List.of(FOLLOWER_ID), postId, publishedAt);

        // Mock user service
        when(userServiceClient.getUser(AUTHOR_ID)).thenReturn(AUTHOR_DTO);

        // Test feed service
        List<FeedPostDto> feed = feedService.getFeed(FOLLOWER_ID, null);

        assertFalse(feed.isEmpty());
        assertEquals(postId, feed.get(0).getPostDto().id());
        assertEquals(AUTHOR_ID, feed.get(0).getAuthor().id());

         */
    }

    private static void sleep(long second) {
        try {
            Thread.sleep(second * 1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}