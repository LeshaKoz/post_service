package faang.school.postservice.publisher.comment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.events.NotificationCommentEvent;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.publisher.EventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationCommentEventPublisher implements EventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final CommentMapper commentMapper;
    private final ObjectMapper objectMapper;

    @Value("${spring.kafka.topics.notifications-comment-topic.name}")
    private String commentNotificationTopic;

    @Override
    public void publishEvent(Object event) {
        try {
            NotificationCommentEvent notificationEvent = commentMapper.toNotificationCommentEvent((Comment) event);
            String json = objectMapper.writeValueAsString(notificationEvent);
            kafkaTemplate.send(commentNotificationTopic, json);
        } catch (JsonProcessingException e) {
            log.warn("Error parsing event: {}", event);
            throw new RuntimeException(e);
        }
    }
}
