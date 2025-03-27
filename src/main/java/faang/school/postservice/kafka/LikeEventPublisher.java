package faang.school.postservice.kafka;

import faang.school.postservice.model.LikeEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LikeEventPublisher implements EventPublisher<LikeEvent> {
    private final KafkaTemplate<String, LikeEvent> likeEventKafkaTemplate;
    private final NewTopic likeTopic;

    public LikeEventPublisher(KafkaTemplate<String, LikeEvent> kafkaTemplate,
                              @Qualifier("likeTopic") NewTopic likeTopic) {
        this.likeEventKafkaTemplate = kafkaTemplate;
        this.likeTopic = likeTopic;
    }

    @Override
    public void publish(LikeEvent message) {
        likeEventKafkaTemplate.send(likeTopic.name(), message);
        log.info("Sent to {} {}", likeTopic.name(), message);
    }
}
