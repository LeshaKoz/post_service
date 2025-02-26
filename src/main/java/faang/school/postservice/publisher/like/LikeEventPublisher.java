package faang.school.postservice.publisher.like;

import faang.school.postservice.event.LikeEvent;
import faang.school.postservice.publisher.MessagePublisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class LikeEventPublisher extends MessagePublisher<LikeEvent> {
    public LikeEventPublisher(
            RedisTemplate<String, Object> redisTemplate,
            @Value("${spring.data.redis.channels.likes}") String channel
    ) {
        super(redisTemplate, channel);
    }
}
