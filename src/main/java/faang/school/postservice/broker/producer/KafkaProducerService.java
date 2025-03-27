package faang.school.postservice.broker.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public abstract class KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    protected final ObjectMapper objectMapper;

    protected void sendMessage(String topic, String message) {

        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, message);

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Failed to send message {} to kafka topic {}. Error: {}",
                        message, topic, ex.getMessage());
            } else {
                log.info("Successfully sent message {} to kafka topic {}, partition {}. Result: {}",
                        message, topic, result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
            }
        });
    }
}
