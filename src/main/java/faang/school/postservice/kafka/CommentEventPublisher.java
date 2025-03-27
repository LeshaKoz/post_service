package faang.school.postservice.kafka;

import faang.school.postservice.model.CommentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommentEventPublisher implements EventPublisher<CommentEvent> {
    @Value("${spring.kafka.topics.comment.name}")
    private String topic;

    private final KafkaTemplate<String, CommentEvent> commentEventKafkaTemplate;

    @Override
    public void publish(CommentEvent message) {
        commentEventKafkaTemplate.send(topic, message);
        log.info("Sent comment event: {} to {}", message, topic);
    }
}
