package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostReadDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.mapper.UserMapper;
import faang.school.postservice.model.cache.UserCache;
import faang.school.postservice.publisher.kafka.feed.FeedHeaterEventPublisher;
import faang.school.postservice.repository.cache.RedisFeedRepository;
import faang.school.postservice.repository.cache.RedisPostRepository;
import faang.school.postservice.repository.cache.RedisUserRepository;
import faang.school.postservice.service.cache.RedisCacheService;
import faang.school.postservice.service.post.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FeedServiceTest {

    @Mock
    private RedisFeedRepository redisFeedRepository;
    @Mock
    private RedisCacheService redisCacheService;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PostMapper postMapper;
    @Mock
    private RedisUserRepository redisUserRepository;
    @Mock
    private RedisPostRepository redisPostRepository;
    @Mock
    private PostService postService;
    @Mock
    private FeedHeaterEventPublisher feedHeaterEventPublisher;

    @InjectMocks
    private FeedService feedService;

    private Long userId;

    @BeforeEach
    void setup() {
        userId = 1L;
        ReflectionTestUtils.setField(feedService, "FEED_BATCH_SIZE", 3);
        ReflectionTestUtils.setField(feedService, "DEFAULT_POST_ID", 1L);
        ReflectionTestUtils.setField(feedService, "NUM_USERS_PAGE", 3);
        ReflectionTestUtils.setField(feedService, "PARTITION_SIZE", 20);
    }

    @Test
    void testGetUserFeedFromCache() {
        Set<Long> cachedPostIds = Set.of(1L, 2L, 3L);

        when(redisFeedRepository.findPostsId(userId)).thenReturn(cachedPostIds);
        when(redisFeedRepository.getRange(userId, 0, 2)).thenReturn(Set.of(1L, 2L));
        when(redisCacheService.getPostCacheByIds(anyList())).thenReturn(List.of(
                new PostReadDto(), new PostReadDto()
        ));

        List<PostReadDto> result = feedService.getUserFeed(userId, null);

        assertThat(result).hasSize(2);
        verify(redisCacheService).getPostCacheByIds(any());
    }

    @Test
    void testGetUserFeedWhenUserNotFoundInRedis() {
        when(redisFeedRepository.findPostsId(userId)).thenReturn(Collections.emptySet());
        when(redisUserRepository.findById(userId)).thenReturn(Optional.empty());
        when(redisUserRepository.findById(userId))
                .thenReturn(Optional.ofNullable(UserCache.builder()
                        .userId(1L)
                        .build()));

        List<PostReadDto> result = feedService.getUserFeed(userId, null);

        assertThat(result).isEmpty();
        verify(redisCacheService, never()).getPostCacheByIds(any());
    }

    @Test
    void testGetUserFeedWhenCacheIsEmpty() {
        Set<Long> emptySet = Collections.emptySet();

        when(redisFeedRepository.findPostsId(userId)).thenReturn(emptySet);
        when(redisUserRepository.findById(userId))
                .thenReturn(Optional.ofNullable(UserCache.builder()
                        .userId(1L)
                        .build()));

        List<PostReadDto> result = feedService.getUserFeed(userId, null);

        assertThat(result).isEmpty();
    }

    @Test
    void testAddPostToSubscriberFeed() {
        long postId = 100L;
        long subscriberId = 200L;
        Set<Long> posts = Set.of(1L, 2L);

        when(redisFeedRepository.findPostsId(subscriberId)).thenReturn(posts);

        feedService.addPostToAuthorSubscribers(postId, List.of(subscriberId));

        verify(redisFeedRepository).checkMaxFeedSize(posts, subscriberId);
        verify(redisFeedRepository).add(subscriberId, postId);
    }

    @Test
    void testAddPostToSubscriberFeedWhenFeedIsEmpty() {
        long postId = 100L;
        long subscriberId = 200L;

        when(redisFeedRepository.findPostsId(subscriberId)).thenReturn(Collections.emptySet());

        feedService.addPostToAuthorSubscribers(postId, List.of(subscriberId));

        verify(redisFeedRepository).checkMaxFeedSize(Collections.emptySet(), subscriberId);
        verify(redisFeedRepository).add(subscriberId, postId);
    }

    @Test
    void testInitFeedHeater() {
        UserDto userDto = new UserDto(1L, "Test", "User", List.of(1L, 2L, 3L));
        Page<UserDto> page = new PageImpl<>(List.of(userDto));

        when(userServiceClient.getAllUsersCount()).thenReturn(3L);
        when(userServiceClient.getUsers(anyInt(), anyInt())).thenReturn(page);
        when(redisUserRepository.saveAll(any())).thenReturn(List.of(new UserCache()));
        when(userMapper.toUserCache(userDto)).thenReturn(new UserCache());

        feedService.initFeedHeater();

        verify(feedHeaterEventPublisher,  times(2)).publish(any());
    }

    @Test
    void testInitFeedHeaterWhenUserPageIsNull() {
        when(userServiceClient.getAllUsersCount()).thenReturn(3L);
        when(userServiceClient.getUsers(anyInt(), anyInt())).thenReturn(null);

        assertThatThrownBy(() -> feedService.initFeedHeater())
                .isInstanceOf(NullPointerException.class);
    }
}
