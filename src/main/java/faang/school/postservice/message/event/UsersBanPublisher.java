package faang.school.postservice.message.event;

import faang.school.postservice.dto.user.UsersBanEvent;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.RedisConnectionFailureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class UsersBanPublisher {

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.data.redis.channels.user-ban-channel.name}")
    private String usersBanTopic;

    @Retryable(
            maxAttemptsExpression = "#{${retry.max-attempts}}",
            backoff = @Backoff(multiplierExpression = "#{${retry.backoff-multiplier}}"),
            retryFor = {RedisConnectionFailureException.class},
            exclude = {NullPointerException.class, IllegalArgumentException.class}
    )
    public void publish(UsersBanEvent usersBanEvent) {
        log.info("Uploading an event to ban users: {}", usersBanEvent);
        redisTemplate.convertAndSend(usersBanTopic, usersBanEvent);
    }
}
