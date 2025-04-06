package faang.school.postservice.publisher.kafka.comment;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.kafka.KafkaProperties;
import faang.school.postservice.event.comment.CommentEvent;
import faang.school.postservice.publisher.kafka.KafkaPublisher;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class CommentEventPublisher extends KafkaPublisher<CommentEvent> {
    public CommentEventPublisher(KafkaTemplate<String, Object> kafkaTemplate,
                                 KafkaProperties kafkaProperties,
                                 ObjectMapper objectMapper) {
        super(kafkaTemplate, kafkaProperties, objectMapper);
    }
}
