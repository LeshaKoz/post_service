package faang.school.postservice.service.kafka;

import faang.school.postservice.dto.event.CommentEventDto;
import faang.school.postservice.mapper.comment.CommentEventMapper;
import faang.school.postservice.model.Comment;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Aspect
public class KafkaCommentEventPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final CommentEventMapper commentMapper;

    @Value("${kafka.topic.comment}")
    private String topic;

    @AfterReturning(pointcut = "@annotation(faang.school.aspect.CreateComment)", returning = "comment")
    public void publishCommentEvent(Comment comment) {
        String uniqueKey = comment.getId().toString();
        CommentEventDto dto = commentMapper.toDto(comment);
        kafkaTemplate.send(topic, uniqueKey, dto);
    }
}
