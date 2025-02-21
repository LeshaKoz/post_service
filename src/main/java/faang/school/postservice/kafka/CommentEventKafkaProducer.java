package faang.school.postservice.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.event.CommentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommentEventKafkaProducer implements KafkaProducer<CommentEvent> {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${kafka.comment.topic}")
    private String topic;

    @Override
    public void produce(CommentEvent event) throws JsonProcessingException {
        log.info("Publishing comment event: {} to topic: {}", event, topic);
        kafkaTemplate.send(topic, objectMapper.writeValueAsString(event));
    }
}
