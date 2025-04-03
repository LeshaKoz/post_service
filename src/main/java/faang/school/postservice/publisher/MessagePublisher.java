package faang.school.postservice.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public abstract class MessagePublisher<T> {
    protected final KafkaTemplate<String, Object> kafkaTemplate;
    protected final String topic;

    public abstract void publish(T message);
}
