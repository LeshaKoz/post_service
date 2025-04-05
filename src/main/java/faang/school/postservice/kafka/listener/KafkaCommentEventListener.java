package faang.school.postservice.kafka.listener;

import faang.school.postservice.dto.event.CommentEvent;
import faang.school.postservice.mapper.event.EventMapper;
import faang.school.postservice.service.event.CommentEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaCommentEventListener implements KafkaEventListener {

    private final EventMapper<CommentEvent> eventMapper;
    private final CommentEventService commentEventService;

    @KafkaListener(topics = "${kafka.comment.topic}", groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaManualAckListenerContainerFactory")
    @Override
    public void listen(@Payload String message, Acknowledgment ack) {
        CommentEvent commentEvent = eventMapper.mapMessageToEvent(message, CommentEvent.class);
        try {
            log.info("Received post event: {}", commentEvent);
            commentEventService.addCommentToPostToFeed(commentEvent);
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Failed add comment with id = {} to post with id = {}", commentEvent.commentId(), commentEvent.postId());
        }
    }
}
