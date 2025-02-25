package faang.school.postservice.message.consumer;

import faang.school.postservice.message.event.PostViewEvent;
import faang.school.postservice.service.NewsFeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class PostViewConsumer {

    private final NewsFeedService newsFeedService;

    @KafkaListener(topics = "${spring.kafka.topic.view-topic.name}")
    public void consume(PostViewEvent postViewEvent, Acknowledgment ack) {
        log.debug("Received post view event {} ", postViewEvent);
        try {
            newsFeedService.incrementViewCount(postViewEvent.postId());
            ack.acknowledge();
        } catch (Exception ex) {
            log.error("Failed to process post view event {}: {}", postViewEvent, ex.getMessage(), ex);
        }
    }
}
