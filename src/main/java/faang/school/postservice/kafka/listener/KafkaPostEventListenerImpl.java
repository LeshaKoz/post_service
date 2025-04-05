package faang.school.postservice.kafka.listener;

import faang.school.postservice.dto.kafka.PostEvent;
import faang.school.postservice.mapper.event.EventMapper;
import faang.school.postservice.service.event.PostEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaPostEventListenerImpl implements KafkaEventListener {

    private final EventMapper<PostEvent> eventMapper;
    private final PostEventService postEventService;

    @Value("${feed.max-size}")
    private int maxFeedSize;

    @KafkaListener(topics = "${kafka.post.topic}", groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaManualAckListenerContainerFactory")
    @Override
    public void listen(@Payload String message, Acknowledgment ack) {
        PostEvent postEvent = eventMapper.mapMessageToEvent(message, PostEvent.class);
        try {
            log.info("Received post event: {}", postEvent);
            postEventService.addPostsToFeed(postEvent);
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Failed add post with id = {}} to feed", postEvent.postId());
        }
    }
}
