package faang.school.postservice.repository;

import faang.school.postservice.model.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RedisPostCacheRepositoryImpl implements PostCacheRepository {
    private final RedisTemplate<String, Post> redisTemplate;

    @Override
    public void save(Post post, Duration ttl) {
        String key = "post:" + post.getId();
        redisTemplate.opsForValue().set(key, post, ttl);
    }

    @Override
    public Optional<Post> findById(Long id) {
        String key = "post:" + id;
        Post post = redisTemplate.opsForValue().get(key);
        return Optional.ofNullable(post);
    }

    @Override
    public void deleteById(Long id) {
        String key = "post:" + id;
        redisTemplate.delete(key);
    }

    @Override
    public void removeExpiredPosts() {
        log.debug("Redis automatically handles expiration based on TTL");

        Set<String> keys = redisTemplate.keys("post:*");
        if (keys.isEmpty()) {
            return;
        }

        int count = 0;
        for (String key : keys) {
            long ttl = redisTemplate.getExpire(key);
            // Remove keys with less than 5 minutes remaining
            if (ttl >= 0 && ttl < 300) {
                redisTemplate.delete(key);
                count++;
            }
        }

        if (count > 0) {
            log.info("Proactively removed {} soon-to-expire posts", count);
        }
    }
}
