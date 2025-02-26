package faang.school.postservice.publisher.comment;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.redis.Channels;
import faang.school.postservice.dto.comment.CommentEvent;
import faang.school.postservice.publisher.AbstractEventPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class CommentEventPublisher extends AbstractEventPublisher<CommentEvent> {
    private final Channels channels;

    public CommentEventPublisher(RedisTemplate<String, Object> redisTemplate,
                                 ObjectMapper objectMapper,
                                 Channels channels) {
        super(redisTemplate, objectMapper);
        this.channels = channels;
    }

    @Override
    public void publish(CommentEvent event) {
        handleEvent(event, channels.getCommentChannel());
    }
}
