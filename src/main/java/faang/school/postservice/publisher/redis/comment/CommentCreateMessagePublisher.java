package faang.school.postservice.publisher.redis.comment;

import faang.school.postservice.event.comment.CommentEvent;
import faang.school.postservice.publisher.redis.RedisPublisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class CommentCreateMessagePublisher extends RedisPublisher<CommentEvent> {
    public CommentCreateMessagePublisher(
            RedisTemplate<String, Object> redisTemplate,
            @Value("${spring.data.redis.channels.comment_create}") String channel
    ) {
        super(redisTemplate, channel);
    }
}
