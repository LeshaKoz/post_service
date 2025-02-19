package faang.school.postservice.publisher;

import faang.school.postservice.dto.messaging.LikeEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LikeEventPublisher {

    @Value("${spring.data.redis.channels.likes}")
    private String likesChannel;

    private final RedisTemplate<String, Object> redisTemplate;

    public void publish(LikeEvent event) {
        redisTemplate.convertAndSend(likesChannel, event);
    }
}
