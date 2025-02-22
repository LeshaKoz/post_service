package faang.school.postservice.publisher.comment;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.event.NotificationLikeEvent;
import faang.school.postservice.event.AnalyticsCommentEvent;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.publisher.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AnalyticsCommentEventPublisher implements EventPublisher {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final CommentMapper commentMapper;

    @Value("${spring.kafka.topics.analytics-comment-topic.name}")
    private String analyticsCommentTopicName;

    @Override
    public void publishEvent(Object dto) {
        try {
            AnalyticsCommentEvent event = commentMapper.toAnalyticsCommentEvent((Comment) dto);
            String jsonEvent = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(analyticsCommentTopicName, jsonEvent);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
