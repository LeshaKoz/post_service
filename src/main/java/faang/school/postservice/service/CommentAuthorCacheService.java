package faang.school.postservice.service;

import faang.school.postservice.config.redis.RedisConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class CommentAuthorCacheService {
    private static final String AUTHORS_KEY = "authors";
    private final StringRedisTemplate redisTemplate;
    private final RedisConfig redisConfig;

    public void cacheCommentAuthor(Long commentId, Long authorId) {
        String key = AUTHORS_KEY + ":" + commentId;
        redisTemplate.opsForValue().set(key, authorId.toString(), redisConfig.getAuthorsTtl(), TimeUnit.SECONDS);
    }

    public String getAuthorFromCache(Long commentId) {
        return redisTemplate.opsForValue().get(AUTHORS_KEY + ":" + commentId);
    }
}

