package faang.school.postservice.service;

import faang.school.postservice.event.PostEvent;
import faang.school.postservice.exception.FeedStorageException;
import faang.school.postservice.exception.InvalidPostEventException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.integration.support.locks.LockRegistry;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.locks.Lock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FeedServiceTest {

    private static final long SUBSCRIBER_ID = 1L;
    private static final long AUTHOR_ID = 2L;
    private static final long POST_ID = 10L;
    private static final long MAX_FEED_SIZE = 500L;
    private static final String FEED_KEY = "feed:" + SUBSCRIBER_ID;
    private static final String LOCK_KEY = "feed:lock:" + SUBSCRIBER_ID;
    private static final String INVALID_POST_EVENT_MESSAGE = "PostEvent или createdAt не может быть равно null";
    private static final String FEED_STORAGE_ERROR_MESSAGE = "Не удалось обновить ленту для ID подписчика: "
            + SUBSCRIBER_ID;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private LockRegistry lockRegistry;

    @Mock
    private Lock lock;

    @Mock
    private ZSetOperations<String, Object> zSetOperations;

    @InjectMocks
    private FeedService feedService;

    private PostEvent validPostEvent;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(feedService, "maxFeedSize", MAX_FEED_SIZE);

        validPostEvent = new PostEvent(
                AUTHOR_ID,
                POST_ID,
                List.of(SUBSCRIBER_ID),
                LocalDateTime.now()
        );
    }

    private void verifyLockOperations() {
        verify(lockRegistry).obtain(LOCK_KEY);
        verify(lock).lock();
        verify(lock).unlock();
    }

    private void mockCommonRedisAndLockBehavior() {
        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
        when(lockRegistry.obtain(LOCK_KEY)).thenReturn(lock);
        doNothing().when(lock).lock();
        doNothing().when(lock).unlock();
    }

    @Test
    void addPostToFeedTest_postEventIsValid() {
        mockCommonRedisAndLockBehavior();
        when(zSetOperations.add(anyString(), anyString(), anyDouble())).thenReturn(true);
        when(zSetOperations.zCard(FEED_KEY)).thenReturn(10L);

        feedService.addPostToFeed(SUBSCRIBER_ID, validPostEvent);

        verifyLockOperations();
        verify(zSetOperations).add(eq(FEED_KEY), eq(String.valueOf(POST_ID)), anyDouble());
        verify(zSetOperations).zCard(FEED_KEY);
        verify(zSetOperations, never()).removeRange(anyString(), anyLong(), anyLong());
    }

    @Test
    void addPostToFeedTest_throwsPostEventIsNull() {

        InvalidPostEventException exception = assertThrows(InvalidPostEventException.class,
                () -> feedService.addPostToFeed(SUBSCRIBER_ID, null)
        );

        assertEquals(INVALID_POST_EVENT_MESSAGE, exception.getMessage());
        verify(lockRegistry, never()).obtain(anyString());
        verify(zSetOperations, never()).add(anyString(), anyString(), anyDouble());
    }

    @Test
    void addPostToFeedTest_throwCreatedAtIsNull() {
        PostEvent postEventWithNullCreatedAt = new PostEvent(
                AUTHOR_ID,
                POST_ID,
                List.of(SUBSCRIBER_ID),
                null
        );

        InvalidPostEventException exception = assertThrows(InvalidPostEventException.class,
                () -> feedService.addPostToFeed(SUBSCRIBER_ID, postEventWithNullCreatedAt)
        );

        assertEquals(INVALID_POST_EVENT_MESSAGE, exception.getMessage());
        verify(lockRegistry, never()).obtain(anyString());
        verify(zSetOperations, never()).add(anyString(), anyString(), anyDouble());
    }

    @Test
    void addPostToFeedTest_throwRedisFails() {
        mockCommonRedisAndLockBehavior();
        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
        doThrow(new RuntimeException("Redis error")).when(zSetOperations)
                .add(anyString(), anyString(), anyDouble());

        FeedStorageException exception = assertThrows(
                FeedStorageException.class,
                () -> feedService.addPostToFeed(SUBSCRIBER_ID, validPostEvent)
        );

        assertEquals(FEED_STORAGE_ERROR_MESSAGE, exception.getMessage());
        verifyLockOperations();
        verify(zSetOperations).add(eq(FEED_KEY), eq(String.valueOf(POST_ID)), anyDouble());
    }

    @Test
    void addPostToFeedTest_TrimFeed() {
        mockCommonRedisAndLockBehavior();

        long currentSize = MAX_FEED_SIZE + 10;
        when(zSetOperations.add(anyString(), anyString(), anyDouble())).thenReturn(true);
        when(zSetOperations.zCard(FEED_KEY)).thenReturn(currentSize);

        feedService.addPostToFeed(SUBSCRIBER_ID, validPostEvent);

        verifyLockOperations();
        verify(zSetOperations).add(eq(FEED_KEY), eq(String.valueOf(POST_ID)), anyDouble());
        verify(zSetOperations).zCard(FEED_KEY);
        verify(zSetOperations).removeRange(FEED_KEY, 0, currentSize - MAX_FEED_SIZE - 1);
    }
}