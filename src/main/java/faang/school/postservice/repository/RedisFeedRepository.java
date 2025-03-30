package faang.school.postservice.repository;

import faang.school.postservice.config.redis.CacheProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RedisFeedRepository {
    private final RedisTemplate<String, Object> cacheRedisTemplate;
    private final CacheProperties properties;
    private static final String FEED_KEY_PREFIX = "feed:";

    public void addPostToSubscriber(Long subscriberId, Long postId, LocalDateTime publishedAt) {
        String key = FEED_KEY_PREFIX + subscriberId;
        double score = publishedAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        //cacheRedisTemplate.opsForZSet().add(key, postId, score);
        cacheRedisTemplate.opsForZSet().add(key, Long.valueOf(postId), score);
        log.info("Post {} was added to subscriber {} published {} ", postId, subscriberId, publishedAt);
        cacheRedisTemplate.opsForZSet().removeRange(key, 0, (long) - properties.getFeedMaxSize() - 1);
    }

    public void addPost(List<Long> subscribersIds, Long postId, LocalDateTime publishedAt) {
        subscribersIds.forEach(subscriberId -> addPostToSubscriber(subscriberId, postId, publishedAt));
    }

    public void deletePostFromAllFeeds(Long postId) {
        Set<String> feedKeys = cacheRedisTemplate.keys(FEED_KEY_PREFIX + "*");
        if (feedKeys != null) {
            for (String key : feedKeys) {
                cacheRedisTemplate.opsForZSet().remove(key, postId);
                log.info("Post {} was deleted", postId);
            }
        }
    }

    public List<Long> getPostIds(Long userId, LocalDateTime lastSeenDate, int pageSize) {
        String key = FEED_KEY_PREFIX + userId;
        Set<Object> postIds;
        log.info("getPostIds userId {} lastSeenDate {} pageSize {} ", userId, lastSeenDate, pageSize);

        if (lastSeenDate == null) {
            postIds = cacheRedisTemplate.opsForZSet().reverseRange(key, 0, pageSize);
        } else {
            double maxScore = lastSeenDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() - 1;
            postIds = cacheRedisTemplate.opsForZSet()
                    .reverseRangeByScore(key, 0, maxScore, 0, pageSize);
        }

        log.info("getPosts postIds = {} ", postIds);

        if (postIds == null || postIds.isEmpty()) {
            return Collections.emptyList();
        }

        return postIds.stream()
                .filter(Objects::nonNull)
                .map(id -> ((Number) id).longValue())
                .toList();
    }
}
