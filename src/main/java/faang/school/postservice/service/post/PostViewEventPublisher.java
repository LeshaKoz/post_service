package faang.school.postservice.service.post;

import faang.school.postservice.event.PostViewEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@RequiredArgsConstructor
@Component
public class PostViewEventPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publish(PostViewEvent event) {
        String uniqueKey = UUID.randomUUID().toString();
        kafkaTemplate.send("user-post-viewed", uniqueKey, event);
    }
}
