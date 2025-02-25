package faang.school.postservice.message.consumer;

import faang.school.postservice.message.event.CommentEvent;
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
public class KafkaCommentConsumer {

    private final NewsFeedService newsFeedService;

    @KafkaListener(topics = "${spring.kafka.topic.comment-topic.name}")
    public void consume(CommentEvent commentEvent, Acknowledgment ack) {
        log.debug("Received comment event {} ", commentEvent);
        newsFeedService.addCommentToCache(commentEvent)
                .thenRun(ack::acknowledge)
                .exceptionally(ex -> {
                    log.error("Failed to process comment event {}: {}", commentEvent, ex.getMessage(), ex);
                    return null;
                });
    }
}
