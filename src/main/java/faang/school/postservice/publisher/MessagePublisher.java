package faang.school.postservice.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;

@RequiredArgsConstructor
public abstract class MessagePublisher<T> {
    private final RedisTemplate<String, Object> redisTemplate;
    private final String channel;

    public void publish(T message) {
        redisTemplate.convertAndSend(channel, message);
    }
}
