package faang.school.postservice.publisher.redis;

import faang.school.postservice.publisher.Publisher;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisPublisher implements Publisher {

    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void publish(String channel, String message) {
        redisTemplate.convertAndSend(channel, message);
    }
}
