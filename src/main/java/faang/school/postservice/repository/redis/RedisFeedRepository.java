package faang.school.postservice.repository.redis;

import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

@Repository
public class RedisFeedRepository {
    @Value("${news-feed.keys.feed}")
    private String FEED_KEY;

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisFeedRepository(
        @Qualifier("feedRedis") RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public List<Long> getFeedPostIds(Long userId, Long afterPostId, int limit) {
        String feedKey = FEED_KEY + userId;
        ZSetOperations<String, Object> zSet = redisTemplate.opsForZSet();

        if (afterPostId == null) {
            return zSet.reverseRange(feedKey, 0, limit - 1).stream()
                .map(id -> (Long) id)
                .toList();
        }

        Double score = zSet.score(feedKey, afterPostId.toString());
        if (score == null) {
            return List.of();
        }

        return zSet.reverseRangeByScore(feedKey, 0, score, 0, limit).stream()
            .map(id -> (Long) id)
            .toList();
    }
}
