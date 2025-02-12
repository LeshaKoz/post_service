package faang.school.postservice.service.like;

import faang.school.postservice.event.LikeEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.UUID;

@AllArgsConstructor
@Slf4j
@Service
public class LikeEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private String addLikeEventTopicName;

    public SendResult<String, Object> publish(LikeEvent event) {
        log.info("User {} add like to post {}", event.getUserId(), event.getPostId());
        String uniqueKey = UUID.randomUUID().toString();
        if (addLikeEventTopicName == null) {
            addLikeEventTopicName = "add-like";
        }
        return kafkaTemplate.send(addLikeEventTopicName, uniqueKey, event).join();
    }
}
