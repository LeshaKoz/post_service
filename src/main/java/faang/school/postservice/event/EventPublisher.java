package faang.school.postservice.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
@Component
public class EventPublisher {

    private final KafkaTemplate<String, Event> kafkaTemplate;

    public void publishEvent(String topic, Event event) {
        kafkaTemplate.send(topic, event);
        log.info("Published event to {}: {}", topic, event);
    }
}