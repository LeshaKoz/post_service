package faang.school.postservice.repository;

import faang.school.postservice.model.Post;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Set;

@Repository
public class FeedRepository {

    @Value("${feed.key}")
    private String FEED_KEY;

    @Value("${feed.post.max-size}")
    private int maxFeedSize;

    private final RedisTemplate<String, Object> redisTemplate;

    public FeedRepository(
            @Qualifier("postFeedTemplate") RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void addPostToFeed(Long subscriberId, Post post) {
        String feedKey = getFeedKey(subscriberId);

        LocalDateTime createdAt = post.getCreatedAt();
        long score = createdAt.atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();

        redisTemplate.opsForZSet().add(feedKey, String.valueOf(post.getId()), score);

        Long size = redisTemplate.opsForZSet().size(feedKey);
        if (size != null && size > maxFeedSize) {
            redisTemplate.opsForZSet().removeRange(feedKey, 0, size - maxFeedSize - 1);
        }
    }

    public Set<Object> getFeed(Long subscriberId, int limit) {
        String feedKey = getFeedKey(subscriberId);
        return redisTemplate.opsForZSet().reverseRange(feedKey, 0, limit - 1);
    }

    private String getFeedKey(Long userId) {
        return String.format("%s:%d", FEED_KEY, userId);
    }

}
