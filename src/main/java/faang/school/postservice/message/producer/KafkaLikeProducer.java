package faang.school.postservice.message.producer;

import faang.school.postservice.message.event.LikeEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaLikeProducer {

    private final KafkaTemplate<String, LikeEvent> kafkaTemplate;

    @Value("${spring.kafka.topic.like-topic.name}")
    private String likeTopicName;

    public void publish(LikeEvent likeEvent) {
        kafkaTemplate.send(likeTopicName, likeEvent)
                .exceptionally(ex -> {
                    log.error("Failed to send like event: {}", likeEvent, ex);
                    return null;
                });
    }
}

