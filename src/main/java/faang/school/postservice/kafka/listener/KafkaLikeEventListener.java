package faang.school.postservice.kafka.listener;

import faang.school.postservice.dto.kafka.LikeEvent;
import faang.school.postservice.mapper.event.EventMapper;
import faang.school.postservice.service.event.LikeEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaLikeEventListener implements KafkaEventListener {

    private final EventMapper<LikeEvent> eventMapper;
    private final LikeEventService likeEventService;

    @KafkaListener(topics = "${kafka.like.topic}", groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaManualAckListenerContainerFactory")
    @Override
    public void listen(String message, Acknowledgment ack) {
        try {
            LikeEvent postViewEvent = eventMapper.mapMessageToEvent(message, LikeEvent.class);
            log.info("Received post event: {}", postViewEvent);
            likeEventService.addLikeToPost(postViewEvent);
        } finally {
            ack.acknowledge();
        }
    }
}