package faang.school.postservice.producer;

import faang.school.postservice.exception.KafkaMessageSendingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractEventProducer<T> {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void send(String topicName, T event) {
        log.info("Sending Json event: {} to Kafka topic {}", event, topicName);
        try {
            kafkaTemplate.send(topicName, event).whenComplete((sendResult, exc) -> {
                if (Objects.nonNull(exc)) {
                    log.error("Sending the event \"{}\" to topic \"{}\" failed", event, topicName, exc);
                } else {
                    log.info("Event \"{}\" sent successfully to topic \"{}\"", event, topicName);
                }
            });
        } catch (Exception e) {
            log.error("An error occurred when sending the event to the topic \"{}\"", topicName);
            throw new KafkaMessageSendingException(
                    "An error occurred when sending the event to the topic \"%s\"".formatted(topicName), e
            );
        }
    }

    public void send(String topicName, String messageKey, T event) {
        log.info("Sending Json event: {} to Kafka topic {}", event, topicName);
        try {
            kafkaTemplate.send(topicName, messageKey, event).whenComplete((sendResult, exc) -> {
                if (Objects.nonNull(exc)) {
                    log.error("Sending the event \"{}\" to topic \"{}\" failed", event, topicName, exc);
                } else {
                    log.info("Event \"{}\" sent successfully to topic \"{}\" with messageKey \"{}\"", event, topicName, messageKey);
                }
            });
        } catch (Exception e) {
            log.error("An error occurred when sending the event to the topic \"{}\"", topicName);
            throw new KafkaMessageSendingException(
                    "An error occurred when sending the event to the topic \"%s\"".formatted(topicName), e
            );
        }
    }
}
