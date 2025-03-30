package faang.school.postservice.repository.cache;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class RedisFeedRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.data.redis.feed-cache.key}")
    private String key;
    @Value("${spring.data.redis.feed-cache.max-feed-size}")
    private long maxFeedSize;

    private ZSetOperations<String, Object> opsForZSet;

    @PostConstruct
    private void init() {
        opsForZSet = redisTemplate.opsForZSet();
    }

    public void add(long subscriberId, long postId) {
        opsForZSet.add(key + subscriberId, postId, System.currentTimeMillis());
    }

    public Set<Long> find(long subscriberId) {
        Set<Object> posts = opsForZSet.reverseRange(key + subscriberId, 0, maxFeedSize - 1);
        if (posts != null) {
            return posts.stream()
                    .map(post -> Long.valueOf(String.valueOf(post)))
                    .collect(Collectors.toSet());
        }
        return Collections.emptySet();
    }

    public void checkMaxFeedSize(Set<Long> postsId, long subscriberId) {
        if (postsId != null && postsId.size() >= maxFeedSize) {
            opsForZSet.removeRange(key + subscriberId, 0, 0);
        }
    }
}
