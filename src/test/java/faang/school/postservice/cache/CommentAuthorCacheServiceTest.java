package faang.school.postservice.cache;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.SessionCallback;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentAuthorCacheServiceTest {

    @Mock
    private RedisTemplate<String, Long> redisTemplate;
    @Mock
    private RedisOperations<String, Long> redisOperations;
    @Mock
    private SetOperations<String, Long> setOperations;
    @Mock
    private Appender<ILoggingEvent> mockAppender;

    @Captor
    private ArgumentCaptor<SessionCallback<Object>> sessionCallbackCaptor;

    private CommentAuthorCacheService commentAuthorCacheService;

    private final String testKey = "test_authors";
    private final Duration testTtl = Duration.ofSeconds(3600);

    @BeforeEach
    void setUp() {
        commentAuthorCacheService = new CommentAuthorCacheService(
                redisTemplate,
                testKey,
                testTtl.getSeconds()
        );

        Logger logger = (Logger) LoggerFactory.getLogger(CommentAuthorCacheService.class);
        logger.addAppender(mockAppender);
        logger.setLevel(Level.INFO);
    }

    @Test
    void cacheCommentAuthor_Success() {
        Long authorId = 123L;

        when(redisTemplate.execute(any(SessionCallback.class))).thenAnswer(invocation -> {
            SessionCallback<Object> callback = invocation.getArgument(0);
            return callback.execute(redisOperations);
        });
        when(redisOperations.opsForSet()).thenReturn(setOperations);

        commentAuthorCacheService.cacheCommentAuthor(authorId);

        verify(redisOperations).multi();
        verify(setOperations).add(testKey, authorId);
        verify(redisOperations).expire(testKey, testTtl);
        verify(redisOperations).exec();

        verify(mockAppender).doAppend(argThat(event -> {
            return event.getLevel() == Level.INFO &&
                    event.getFormattedMessage().contains("Caching comment author 123") &&
                    event.getFormattedMessage().contains("test_authors") &&
                    event.getFormattedMessage().contains("3600");
        }));
    }

    @Test
    void cacheCommentAuthor_NullAuthorId() {
        commentAuthorCacheService.cacheCommentAuthor(null);

        verify(redisTemplate, never()).execute(any(SessionCallback.class));

        verify(mockAppender).doAppend(argThat(event -> {
            return event.getLevel() == Level.WARN &&
                    event.getMessage().contains("AuthorId is null");
        }));
    }

    @Test
    void constructor_InitializesCorrectly() {
        assertEquals(testKey, commentAuthorCacheService.authorsKey);
        assertEquals(testTtl, commentAuthorCacheService.ttl);
    }
}