package faang.school.postservice.repository.redis;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RedisFeedRepository {
    @Value("${news-feed.keys.feed}")
    private String FEED_KEY;

    private final RedisTemplate<String, String> redisTemplate;

    public RedisFeedRepository(
        @Qualifier("feedRedis") RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public List<Long> getPostsAfterTimeStamp(Long userId, Long timeStamp, int amountPosts) {
        return redisTemplate.opsForZSet()
            .reverseRangeByScore(feedPrefix(userId), timeStamp, Double.MAX_VALUE, 1, amountPosts)
            .stream()
            .map(Long::valueOf)
            .collect(Collectors.toList());
    }

    public List<Long> getLatestPosts(Long userId, int amountPosts) {
        return redisTemplate.opsForZSet().reverseRange(feedPrefix(userId), 0, amountPosts - 1)
            .stream()
            .map(Long::valueOf)
            .collect(Collectors.toList());
    }

    public void savePostIdToUserFeed(Long userId, Long postId, Long publishedAt) {
        redisTemplate.opsForZSet().add(feedPrefix(userId), postId.toString(), publishedAt);
    }

    private String feedPrefix(Long userId) {
        return String.format("%s:%d", FEED_KEY, userId);
    }
}
