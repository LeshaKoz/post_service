package faang.school.postservice.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
@Component
public class LikeEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishLikeEvent(LikeEvent event) {
        kafkaTemplate.send("like-events", event);
        log.info("Published LikeEvent: {}", event);
    }
}