package faang.school.postservice.message.producer;

import faang.school.postservice.message.event.PostEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaPostProducer {

    private final KafkaTemplate<String, PostEvent> kafkaTemplate;

    @Value("${spring.kafka.topic.post-topic.name}")
    private String postTopicName;

    public void publish(PostEvent postEvent) {
        kafkaTemplate.send(postTopicName, postEvent)
                .exceptionally(ex -> {
                    log.error("Failed to send post event: {}", postEvent, ex);
                    return null;
                });
    }
}