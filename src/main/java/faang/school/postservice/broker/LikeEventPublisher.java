package faang.school.postservice.broker;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LikeEventPublisher implements MessagePublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic likeEventTopic;
    @Override
    public void publish(String message) {
        redisTemplate.convertAndSend(likeEventTopic.getTopic(), message);
    }
}
