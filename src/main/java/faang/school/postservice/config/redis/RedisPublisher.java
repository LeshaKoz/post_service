package faang.school.postservice.config.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisPublisher {

    private final RedisTemplate<String, String> redisTemplate;
    private final ChannelTopic userBanTopic;

    public void publishUserBanEvent(Long userId) {
        log.info("Publishing user ban event for user: {}", userId);
        redisTemplate.convertAndSend(userBanTopic.getTopic(), String.valueOf(userId));
    }
}
