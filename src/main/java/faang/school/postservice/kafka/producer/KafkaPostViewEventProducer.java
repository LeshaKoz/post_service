package faang.school.postservice.kafka.producer;

import faang.school.postservice.dto.event.PostViewEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaPostViewEventProducer {
    private final KafkaTemplate<String, PostViewEvent> kafkaTemplate;

    @Value("${spring.kafka.producer.post-view.topic}")
    private String topic;

    public void send(PostViewEvent postViewEvent) {
        kafkaTemplate.send(topic, postViewEvent);
    }
}
