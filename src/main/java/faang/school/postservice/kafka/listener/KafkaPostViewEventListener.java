package faang.school.postservice.kafka.listener;

import faang.school.postservice.dto.kafka.PostViewsEvent;
import faang.school.postservice.mapper.event.EventMapper;
import faang.school.postservice.service.event.PostViewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaPostViewEventListener implements KafkaEventListener {

    private final EventMapper<PostViewsEvent> eventMapper;
    private final PostViewService postViewService;

    @KafkaListener(topics = "${kafka.post.views.topic}", groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaManualAckListenerContainerFactory")
    @Override
    public void listen(String message, Acknowledgment ack) {
        try {
            PostViewsEvent postViewEvent = eventMapper.mapMessageToEvent(message, PostViewsEvent.class);
            log.info("Received post event: {}", postViewEvent);
            postViewService.addViewToPost(postViewEvent);
        } finally {
            ack.acknowledge();
        }
    }
}