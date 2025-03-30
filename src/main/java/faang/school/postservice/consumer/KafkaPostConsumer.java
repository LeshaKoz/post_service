package faang.school.postservice.consumer;

import faang.school.postservice.event.post.PostCreatedEvent;
import faang.school.postservice.event.post.PostDeletedEvent;
import faang.school.postservice.exception.BusinessException;
import faang.school.postservice.service.feed.FeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaPostConsumer {

    private final FeedService feedService;

    @Retryable(
            retryFor = {DataAccessException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    @KafkaListener(topics = "post-creations", containerFactory = "kafkaListenerContainerFactory")
    public void listenCreations(PostCreatedEvent event, Acknowledgment ack) {
        try {
            feedService.addToFeed(event);
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Не удалось обработать PostCreatedEvent: {}",  event.getPostId(), e);
            throw e;
        }
    }

    @Retryable(
            retryFor = {DataAccessException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    @KafkaListener(topics = "post-deletions", containerFactory = "kafkaListenerContainerFactory")
    public void listenUpdates(PostDeletedEvent event, Acknowledgment ack) {
        try {
            feedService.removeFromFeed(event);
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Не удалось обработать PostDeletedEvent: {}",  event.getPostId(), e);
            throw e;
        }
    }
}
