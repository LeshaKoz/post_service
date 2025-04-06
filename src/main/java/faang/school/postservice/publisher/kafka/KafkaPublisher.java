package faang.school.postservice.publisher.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.kafka.KafkaProperties;
import faang.school.postservice.publisher.EventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public abstract class KafkaPublisher<T> implements EventPublisher<T> {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaProperties kafkaProperties;
    private final ObjectMapper objectMapper;

    @Override
    public void publish(T event) {
        String topicName = kafkaProperties.getTopics()
                .get(event.getClass().getSimpleName()).name();

        kafkaTemplate.send(topicName, serializeEvent(event));
    }

    protected Object serializeEvent(T event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            log.error("Ошибка сериализации в JSON: {}", e.getMessage(), e);
            throw new RuntimeException("Ошибка преобразования события", e);
        }
    }
}
