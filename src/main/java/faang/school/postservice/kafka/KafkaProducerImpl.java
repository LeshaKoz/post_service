package faang.school.postservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaProducerImpl implements KafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public void produce(String topic, String message) {
        log.info("Publishing comment event: {} to topic: {}", message, topic);
        kafkaTemplate.send(topic, message);
    }
}
