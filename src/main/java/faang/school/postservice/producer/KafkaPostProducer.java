package faang.school.postservice.producer;

import faang.school.postservice.dto.feed.FeedPostEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaPostProducer extends AbstractEventProducer<FeedPostEvent> {
    public KafkaPostProducer(KafkaTemplate<String, Object> kafkaTemplate,
                             @Value("${spring.kafka.topics.post.name}") String topic) {
        super(kafkaTemplate, topic);
    }
}
