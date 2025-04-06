package faang.school.postservice.consumer;

import faang.school.postservice.event.post.PostCreatedEvent;
import faang.school.postservice.event.post.PostDeletedEvent;
import faang.school.postservice.service.feed.FeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${spring.kafka.listener.retry.maxAttempts}")
    private int maxAttempts;

    @Value("${spring.kafka.listener.retry.delay}")
    private long delay;

    @Value("${spring.kafka.listener.retry.multiplier}")
    private double multiplier;

    @Retryable(
            retryFor = {DataAccessException.class},
            maxAttemptsExpression = "#{${spring.kafka.listener.retry.maxAttempts}}",
            backoff = @Backoff(delayExpression = "#{${spring.kafka.listener.retry.delay}}",
                    multiplierExpression = "#{${spring.kafka.listener.retry.multiplier}}")
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
            maxAttemptsExpression = "#{${spring.kafka.listener.retry.maxAttempts}}",
            backoff = @Backoff(delayExpression = "#{${spring.kafka.listener.retry.delay}}",
                    multiplierExpression = "#{${spring.kafka.listener.retry.multiplier}}")
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
