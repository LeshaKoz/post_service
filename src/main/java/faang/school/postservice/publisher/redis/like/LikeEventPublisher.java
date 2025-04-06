package faang.school.postservice.publisher.redis.like;

import faang.school.postservice.event.like.LikeEvent;
import faang.school.postservice.publisher.redis.RedisPublisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class LikeEventPublisher extends RedisPublisher<LikeEvent> {
    public LikeEventPublisher(
            RedisTemplate<String, Object> redisTemplate,
            @Value("${spring.data.redis.channels.likes}") String channel
    ) {
        super(redisTemplate, channel);
    }
}
