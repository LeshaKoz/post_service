package faang.school.postservice.publisher.kafka.feed;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.kafka.KafkaProperties;
import faang.school.postservice.event.feed.FeedEvent;
import faang.school.postservice.publisher.kafka.KafkaPublisher;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class FeedHeaterEventPublisher extends KafkaPublisher<FeedEvent> {

    public FeedHeaterEventPublisher(KafkaTemplate<String, Object> kafkaTemplate,
                                    KafkaProperties kafkaProperties,
                                    ObjectMapper objectMapper) {
        super(kafkaTemplate, kafkaProperties, objectMapper);
    }
}
