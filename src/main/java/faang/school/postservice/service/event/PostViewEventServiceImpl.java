package faang.school.postservice.service.event;

import faang.school.postservice.dto.kafka.PostViewsEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostViewEventServiceImpl implements PostViewService {

    private static final String VIEW_KEY_PREFIX = "post::views:";

    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void addViewToPost(PostViewsEvent postViewEvent) {
        String postKey = VIEW_KEY_PREFIX + postViewEvent.postId();
        String viewId = UUID.randomUUID().toString();
        String lockKey = "lock:" + postKey;
        String lockValue = UUID.randomUUID().toString();
        try {
            boolean locked = Boolean.TRUE.equals(
                    redisTemplate.opsForValue().setIfAbsent(lockKey, lockValue, 10, TimeUnit.SECONDS));
            if (!locked) {
                log.error("Could not acquire lock for feed: {}", postKey);
                return;
            }
            redisTemplate.opsForSet().add(postKey, viewId);
        } finally {
            String currentLockValue = redisTemplate.opsForValue().get(lockKey);
            if (lockValue.equals(currentLockValue)) {
                redisTemplate.delete(lockKey);
            }
        }
    }
}
