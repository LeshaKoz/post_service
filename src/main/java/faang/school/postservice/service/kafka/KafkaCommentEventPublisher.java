package faang.school.postservice.service.kafka;

import faang.school.postservice.dto.event.CommentEventDto;
import faang.school.postservice.mapper.comment.CommentEventMapper;
import faang.school.postservice.model.Comment;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Aspect
public class KafkaCommentEventPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final CommentEventMapper commentMapper;
    private static final String TOPIC = "user-comment-post";


    @AfterReturning(pointcut = "@annotation(faang.school.aspect.CreateComment) && args(comment,..)")
    public void publishCommentEvent(Comment comment) {
        String uniqueKey = UUID.randomUUID().toString();
        CommentEventDto dto = commentMapper.toDto(comment);
        kafkaTemplate.send(TOPIC, uniqueKey, dto);
    }
}
