package faang.school.postservice.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;

@Slf4j
@RequiredArgsConstructor
public abstract class KafkaAbstractProducer<T> {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendEvent(T event, NewTopic topicName) {
        log.info("Sent event {} to topic {}", event, topicName);
        kafkaTemplate.send(topicName.name(), event);
    }
}
