package faang.school.postservice.service;

import faang.school.postservice.config.redis.RedisConfig;
import faang.school.postservice.service.CommentAuthorCacheService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CommentAuthorCacheServiceTest {
    @InjectMocks
    private CommentAuthorCacheService commentAuthorCacheService;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private RedisConfig redisConfig;

    @BeforeEach
    void setUp() {
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        lenient().when(redisConfig.getAuthorsTtl()).thenReturn(86400L);

        commentAuthorCacheService = new CommentAuthorCacheService(redisTemplate, redisConfig);
    }

    @Test
    void testCacheCommentAuthor() {
        Long commentId = 1L;
        Long authorId = 42L;
        String key = "authors:1";

        commentAuthorCacheService.cacheCommentAuthor(commentId, authorId);

        verify(valueOperations, times(1))
                .set(key, authorId.toString(), 86400L, TimeUnit.SECONDS);
    }

    @Test
    void testGetAuthorFromCache() {
        Long commentId = 1L;
        String expectedAuthorId = "42";
        String key = "authors:1";

        when(valueOperations.get(key)).thenReturn(expectedAuthorId);

        String actualAuthorId = commentAuthorCacheService.getAuthorFromCache(commentId);

        assertEquals(expectedAuthorId, actualAuthorId);
        verify(valueOperations, times(1)).get(key);
    }
}

