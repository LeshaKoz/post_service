package faang.school.postservice.message.consumer;


import faang.school.postservice.message.event.LikeEvent;
import faang.school.postservice.service.NewsFeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaLikeConsumer {

    private final NewsFeedService newsFeedService;

    @KafkaListener(topics = "${spring.kafka.topic.like-topic.name}")
    public void consume(LikeEvent likeEvent, Acknowledgment ack) {
        log.debug("Received like event {} ", likeEvent);
        try {
            newsFeedService.incrementLikeCount(likeEvent.postId());
            ack.acknowledge();
        } catch (Exception ex) {
            log.error("Failed to process like event {}: {}", likeEvent, ex.getMessage(), ex);
        }
    }
}
