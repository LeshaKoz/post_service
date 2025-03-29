package faang.school.postservice.kafka;

import faang.school.postservice.model.PostEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostEventPublisher implements EventPublisher<PostEvent> {
    private final KafkaTemplate<String, PostEvent> postEventKafkaTemplate;

    @Value("${spring.kafka.topics.post.name}")
    private String topic;

    @Override
    public void publish(PostEvent message) {
        postEventKafkaTemplate.send(topic, message);
    }
}
