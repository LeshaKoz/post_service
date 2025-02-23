package faang.school.postservice.publisher.comment;

import faang.school.postservice.event.comment.CommentEvent;
import faang.school.postservice.publisher.MessagePublisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class CommentCreateMessagePublisher extends MessagePublisher<CommentEvent> {
    public CommentCreateMessagePublisher(
            RedisTemplate<String, Object> redisTemplate,
            @Value("${spring.data.redis.channels.comment_create}") String channel
    ) {
        super(redisTemplate, channel);
    }
}
