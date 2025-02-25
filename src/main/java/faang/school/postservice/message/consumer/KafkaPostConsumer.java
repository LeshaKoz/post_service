package faang.school.postservice.message.consumer;

import faang.school.postservice.message.event.PostEvent;
import faang.school.postservice.service.NewsFeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaPostConsumer {

    private final NewsFeedService newsFeedService;

    @KafkaListener(topics = "${spring.kafka.topic.post-topic.name}")
    public void consume(PostEvent postEvent, Acknowledgment ack) {
        log.debug("Received post event {} ", postEvent);
        newsFeedService.addPostToFollowersFeedInCache(postEvent)
                .thenRun(ack::acknowledge)
                .exceptionally(ex -> {
                    log.error("Failed to process post event {}: {}", postEvent, ex.getMessage(), ex);
                    return null;
                });
    }
}