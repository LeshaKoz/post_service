package faang.school.postservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.comment.CommentCreateEventDto;
import faang.school.postservice.dto.like.LikePostEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaService {

    @Value("${spring.kafka.topics.comment_create_event}")
    private String commentCreateTopic;
    @Value("${spring.kafka.topics.post_like_event}")
    private String postLikeTopic;

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
    public void sendPostLikeMessage(LikePostEvent likePostEvent) {
        try {
            String message = objectMapper.writeValueAsString(likePostEvent);
            kafkaTemplate.send(postLikeTopic, message);
            log.info("Sent post like message {} to topic: {}", message, postLikeTopic);
        } catch (Exception e) {
            log.error("Error while sending post like message {}", e.getMessage());
            throw new RuntimeException("Error while sending post like message");
        }
    }
}
