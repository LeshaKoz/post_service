package faang.school.postservice.kafka.album;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.kafka.AlbumCreatedEvent;
import faang.school.postservice.kafka.KafkaProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AlbumCreatedEventKafkaProducer implements KafkaProducer<AlbumCreatedEvent> {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${kafka.album.created.topic}")
    private String topic;

    @Override
    public void produce(AlbumCreatedEvent event) throws JsonProcessingException {
        log.info("Publishing event {} to topic: {}", event, topic);
        kafkaTemplate.send(topic, objectMapper.writeValueAsString(event));
    }
}
