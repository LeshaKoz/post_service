package faang.school.postservice.publisher;

import faang.school.postservice.event.CommentEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class CommentMessagePublisher extends MessagePublisher<CommentEvent> {
    public CommentMessagePublisher(
            RedisTemplate<String, Object> redisTemplate,
            @Value("${spring.data.redis.topics.comment}") String channel
    ) {
        super(redisTemplate, channel);
    }
}
