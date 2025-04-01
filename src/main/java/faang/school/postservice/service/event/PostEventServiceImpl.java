package faang.school.postservice.service.event;

import faang.school.postservice.dto.kafka.PostEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostEventServiceImpl implements PostEventService {
    private static final String FEED_KEY_PREFIX = "user:feed:";

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${feed.max-size}")
    private int maxFeedSize;

    @Override
    public void addPostsToFeed(PostEvent event) {
        try {
            for (Long followerId : event.followersIds()) {
                String feedKey = FEED_KEY_PREFIX + followerId;
                String lockKey = "lock:" + feedKey;
                String lockValue = UUID.randomUUID().toString();
                try {
                    boolean locked = Boolean.TRUE.equals(
                            redisTemplate.opsForValue().setIfAbsent(lockKey, lockValue, 10, TimeUnit.SECONDS));
                    if (!locked) {
                        log.error("Could not acquire lock for feed: {}", feedKey);
                        return;
                    }
                    if (isDuplicate(event, feedKey)) {
                        continue;
                    }
                    addPostToFeedAndSortedByCreatedDate(event, feedKey);
                    checkFeedSize(feedKey);
                } finally {
                    String currentLockValue = redisTemplate.opsForValue().get(lockKey);
                    if (lockValue.equals(currentLockValue)) {
                        redisTemplate.delete(lockKey);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Failed add post with id = {} to feed at redis", event.postId(), e);
        }
    }

    private void addPostToFeedAndSortedByCreatedDate(PostEvent event, String feedKey) {
        redisTemplate.opsForZSet().add(feedKey, String.valueOf(event.postId()),
                -event.createdAt().toEpochSecond(ZoneOffset.UTC));
    }

    private void checkFeedSize(String feedKey) {
        Long size = redisTemplate.opsForZSet().zCard(feedKey);
        if (size != null && size > maxFeedSize) {
            redisTemplate.opsForZSet().removeRange(feedKey, maxFeedSize, -1);
        }
    }

    private boolean isDuplicate(PostEvent event, String feedKey) {
        Double existingScore = redisTemplate.opsForZSet().score(feedKey, String.valueOf(event.postId()));
        return existingScore != null;
    }
}
