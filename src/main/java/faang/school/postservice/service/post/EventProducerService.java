package faang.school.postservice.service.post;

import faang.school.postservice.event.PostViewEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class EventProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    public EventProducerService(
            @Qualifier("postViewEventTemplate") KafkaTemplate<String, Object> postViewKafkaTemplate) {
        this.kafkaTemplate = postViewKafkaTemplate;
    }

    @Value("${spring.kafka.topics.analytic-topics.post-view-topic}")
    private String postViewTopic;

    public void publish(PostViewEvent event) {
        String messageKey = event.getPostId().toString();
        kafkaTemplate.send(postViewTopic, messageKey, event);
    }
}