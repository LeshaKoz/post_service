package faang.school.postservice.service.post;

import faang.school.postservice.event.PostViewEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class EventProducerService {

    private final KafkaTemplate<String, Object> postViewKafkaTemplate;

    @Autowired
    public EventProducerService(
            @Qualifier("postViewEventTemplate") KafkaTemplate<String, Object> postViewKafkaTemplate) {
        this.postViewKafkaTemplate = postViewKafkaTemplate;
    }

    @Value("${spring.kafka.post-view-topic}")
    private String postViewTopic;

    public void publish(PostViewEvent event) {
        String uniqueKey = UUID.randomUUID().toString();
        postViewKafkaTemplate.send(postViewTopic, uniqueKey, event);
    }
}