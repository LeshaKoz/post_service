package faang.school.postservice.message.producer;

import faang.school.postservice.message.event.CommentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaCommentProducer {

    private final KafkaTemplate<String, CommentEvent> kafkaTemplate;

    @Value("${spring.kafka.topic.comment-topic.name}")
    private String commentTopicName;

    public void publish(CommentEvent commentEvent) {
        kafkaTemplate.send(commentTopicName, commentEvent)
                .exceptionally(ex -> {
                    log.error("Failed to send comment event: {}", commentEvent, ex);
                    return null;
                });
    }
}
