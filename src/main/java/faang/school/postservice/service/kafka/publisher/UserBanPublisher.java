package faang.school.postservice.service.kafka.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserBanPublisher {
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${spring.kafka.producer.topics.user-ban}")
    private String topic;

    public void publish(Long userId) {
        log.info("User with ID {} sent to user_service for ban", userId);
        kafkaTemplate.send(topic, String.valueOf(userId));
    }
}
