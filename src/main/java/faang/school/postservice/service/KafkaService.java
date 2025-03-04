package faang.school.postservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.comment.CommentCreateEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaService {

    @Value("${spring.kafka.topics.comment_create}")
    private String commentCreateTopic;

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void sendCommentCreateMessage(CommentCreateEventDto commentCreateEventDto) {
        try {
            String message = objectMapper.writeValueAsString(commentCreateEventDto);
            kafkaTemplate.send(commentCreateTopic, message);
            log.info("Sent comment create message {} to topic: {}", message, commentCreateTopic);
        } catch (Exception e) {
            log.error("Error while sending comment create message {}", e.getMessage());
            throw new RuntimeException("Error while sending comment create message");
        }
    }
}
