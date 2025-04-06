package faang.school.postservice.publisher.redis;

import faang.school.postservice.publisher.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;

@RequiredArgsConstructor
public abstract class RedisPublisher<T> implements EventPublisher<T> {
    private final RedisTemplate<String, Object> redisTemplate;
    private final String channel;

    @Override
    public void publish(T event) {
        redisTemplate.convertAndSend(channel, event);
    }
}
