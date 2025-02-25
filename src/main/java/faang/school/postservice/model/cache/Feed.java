package faang.school.postservice.model.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.Set;  
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class Feed {

    private static final String FEED_PREFIX = "feed:";
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${feed.size}")
    private int maxFeedSize;

    public void addPostToFeed(Long userId, Long postId) {
        String key = FEED_PREFIX + userId;
        redisTemplate.opsForZSet().add(key, postId, System.currentTimeMillis());
        log.debug("Post with ID {} added to the feed for user {}", postId, userId);

        cleanUpOldPosts(key);
    }

    @Async("cleanOldPostsPool")
    public void cleanUpOldPosts(String key) {
        Long currentSize = redisTemplate.opsForZSet().size(key);
        if (currentSize != null && currentSize > maxFeedSize) {
            log.debug("Removing unnecessary posts from the user {} cache", key);
            long elementsToRemove = currentSize - maxFeedSize;
            redisTemplate.opsForZSet().removeRange(key, 0, elementsToRemove - 1);
        }
    }

    public Set<Long> getLastNPosts(Long userId, int count) {
        String key = FEED_PREFIX + userId;
        log.debug("Retrieved last {} posts for user {}", count, userId);

        return redisTemplate.opsForZSet()
                .reverseRange(key, 0, count - 1)
                .stream()
                .map(o -> Long.valueOf(o.toString()))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
