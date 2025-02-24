package faang.school.postservice.config.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${spring.kafka.user-ban-topic}")
    private String userTopic;

    public void sendEventToUserServiceForBan(Long userId) {
        log.info("Publishing user ban event for user: {}", userId);
        kafkaTemplate.send(userTopic, userId.toString());
    }
}
