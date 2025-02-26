package faang.school.postservice.service.like;

import faang.school.postservice.event.LikeEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class LikeEventPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String addLikeEventTopicName;

    public LikeEventPublisher(@Qualifier("addLikeKafkaTemplate") KafkaTemplate<String, Object> kafkaTemplate,
                              String addLikeEventTopicName) {
        this.kafkaTemplate = kafkaTemplate;
        this.addLikeEventTopicName = addLikeEventTopicName;
    }

    public SendResult<String, Object> publish(LikeEvent event) {
        log.info("User {} add like to post {}", event.getUserId(), event.getPostId());
        String uniqueKey = UUID.randomUUID().toString();
        return kafkaTemplate.send(addLikeEventTopicName, uniqueKey, event).join();
    }
}
