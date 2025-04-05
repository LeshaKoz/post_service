package faang.school.postservice.service;

import faang.school.postservice.event.PostEvent;
import faang.school.postservice.event.PostViewEvent;
import faang.school.postservice.exception.FeedStorageException;
import faang.school.postservice.exception.InvalidPostEventException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.integration.support.locks.LockRegistry;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.concurrent.locks.Lock;

@Service
@Slf4j
@RequiredArgsConstructor
public class FeedService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final LockRegistry lockRegistry;

    @Value("${feed.max-size}")
    private long maxFeedSize;

    public void addPostToFeed(long subscriberId, PostEvent postEvent) {
        validatePostEvent(postEvent);
        String feedKey = getFeedKey(subscriberId);
        Lock lock = lockRegistry.obtain(getLockKey(subscriberId));

        try {
            lock.lock();
            updateFeed(feedKey, postEvent);
        } catch (Exception e) {
            log.error("Не удалось обновить ленту для ID подписчика: {}", subscriberId, e);
            throw new FeedStorageException("Не удалось обновить ленту для ID подписчика: " + subscriberId, e);
        } finally {
            lock.unlock();
        }
    }

    public void incrementViewCache(PostViewEvent event, Long views){
        String key = "posts:" + event.postId() + ":views";
        redisTemplate.opsForValue().set(key, views);
    }

    private void validatePostEvent(PostEvent postEvent) {
        if (postEvent == null || postEvent.createdAt() == null) {
            throw new InvalidPostEventException("PostEvent или createdAt не может быть равно null");
        }
    }

    private String getFeedKey(long subscriberId) {
        return "feed:" + subscriberId;
    }

    private String getLockKey(long subscriberId) {
        return "feed:lock:" + subscriberId;
    }

    private void updateFeed(String feedKey, PostEvent postEvent) {
        long score = -postEvent.createdAt()
                .atZone(ZoneId.of("UTC"))
                .toInstant()
                .toEpochMilli();
        redisTemplate.opsForZSet().add(feedKey, String.valueOf(postEvent.postId()), score);
        trimFeed(feedKey);
    }

    private void trimFeed(String feedKey) {
        Long size = redisTemplate.opsForZSet().zCard(feedKey);
        if (size != null && size > maxFeedSize) {
            redisTemplate.opsForZSet().removeRange(feedKey, 0, size - maxFeedSize - 1);
        }
    }
}