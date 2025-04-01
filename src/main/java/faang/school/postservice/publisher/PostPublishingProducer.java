package faang.school.postservice.publisher;

import faang.school.postservice.event.PostEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Component
public class PostPublishingProducer extends MessagePublisher<PostEvent> {

    public PostPublishingProducer(
            KafkaTemplate<String, Object> kafkaTemplate,
            @Value("${spring.kafka.topics.post.name}") String topic
    ) {
        super(kafkaTemplate, topic);
    }

    @Override
    @Retryable(
            backoff = @Backoff(delay = 2000)
    )
    public void publish(PostEvent message) {
        kafkaTemplate.send(topic, message).join();
    }
}
