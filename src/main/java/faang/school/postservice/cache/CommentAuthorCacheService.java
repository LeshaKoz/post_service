package faang.school.postservice.cache;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
public class CommentAuthorCacheService {

    private final RedisTemplate<String, Long> redisTemplate;
    final String authorsKey;
    final Duration ttl;

    public CommentAuthorCacheService(RedisTemplate<String, Long> redisTemplate,
                                     @Value("${cache.authors.collection}") String authorsKey,
                                     @Value("${cache.authors.ttl}") long ttlSeconds) {
        this.redisTemplate = redisTemplate;
        this.authorsKey = authorsKey;
        this.ttl = Duration.ofSeconds(ttlSeconds);
    }

    @Async("commentAuthorCacheExecutor")
    public void cacheCommentAuthor(Long authorId) {
        if (authorId == null) {
            log.warn("AuthorId is null. Skipping caching operation.");
            return;
        }

        log.info("Caching comment author {} in key '{}' with TTL {} seconds", authorId, authorsKey, ttl.getSeconds());

        redisTemplate.execute(new SessionCallback<>() {
            @Override
            public Object execute(@NotNull RedisOperations operations) throws DataAccessException {
                operations.multi();
                operations.opsForSet().add(authorsKey, authorId);
                operations.expire(authorsKey, ttl);
                return operations.exec();
            }
        });
    }
}