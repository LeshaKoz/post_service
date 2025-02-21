package faang.school.postservice.service.like;

import faang.school.postservice.event.LikeEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class LikeEventPublisher {

    private final KafkaTemplate<String, LikeEvent> kafkaTemplate;

    public SendResult<String, LikeEvent> publish(LikeEvent event) {
        log.info("User {} add like to post {}", event.getUserId(), event.getPostId());
        return kafkaTemplate.send("likes", event).join();
    }
}
