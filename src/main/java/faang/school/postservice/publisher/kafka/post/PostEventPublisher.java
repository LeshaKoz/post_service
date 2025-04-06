package faang.school.postservice.publisher.kafka.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.kafka.KafkaProperties;
import faang.school.postservice.event.post.PostEvent;
import faang.school.postservice.publisher.kafka.KafkaPublisher;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PostEventPublisher extends KafkaPublisher<PostEvent> {

    public PostEventPublisher(KafkaTemplate<String, Object> kafkaTemplate,
                              KafkaProperties kafkaProperties,
                              ObjectMapper objectMapper) {
        super(kafkaTemplate, kafkaProperties, objectMapper);
    }
}
