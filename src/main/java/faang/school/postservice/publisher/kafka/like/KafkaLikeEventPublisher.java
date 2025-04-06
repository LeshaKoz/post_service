package faang.school.postservice.publisher.kafka.like;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.kafka.KafkaProperties;
import faang.school.postservice.event.like.LikeEvent;
import faang.school.postservice.publisher.kafka.KafkaPublisher;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaLikeEventPublisher extends KafkaPublisher<LikeEvent> {
    public KafkaLikeEventPublisher(KafkaTemplate<String, Object> kafkaTemplate,
                                   KafkaProperties kafkaProperties,
                                   ObjectMapper objectMapper) {
        super(kafkaTemplate, kafkaProperties, objectMapper);
    }
}
