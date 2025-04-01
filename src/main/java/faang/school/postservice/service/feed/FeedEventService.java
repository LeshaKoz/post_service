package faang.school.postservice.service.feed;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.FeedProperties;
import faang.school.postservice.dto.feed.FeedPostDeleteEvent;
import faang.school.postservice.dto.feed.FeedPostEvent;
import faang.school.postservice.dto.user.SubscriptionUserDto;
import faang.school.postservice.producer.KafkaPostDeleteProducer;
import faang.school.postservice.producer.KafkaPostProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
@RequiredArgsConstructor
public class FeedEventService {
    private final UserServiceClient userServiceClient;
    private final KafkaPostProducer kafkaPostProducer;
    private final KafkaPostDeleteProducer kafkaPostDeleteProducer;
    private final FeedProperties properties;

    //@Async()
    @Async("taskExecutor")
    public void createAndSendFeedPostEventForNewPost(Long postId, Long authorId, LocalDateTime publishedAt) {
        createAndSendFeedPostEvent(postId, authorId, publishedAt, properties.getPostTopic());
    }

    private void createAndSendFeedPostEvent(Long postId, Long authorId, LocalDateTime publishedAt, String topicName) {
        log.info("createAndSendFeedPostEvent postId {} authorId {} publishedAt {} topicName {}",
                postId, authorId, publishedAt, topicName);
        List< SubscriptionUserDto> userDtos = userServiceClient.getFollowers(authorId);
        List<Long> subscribersIds = userDtos.stream()
                .map(SubscriptionUserDto::id)
                .toList();
        log.info("subscribersIds {} ", subscribersIds);
        if (subscribersIds.isEmpty()) {
            log.info("Author {} has no subscribers or failed to retrieve subscribers. No events will be sent.", authorId);
        } else if (subscribersIds.size() <= properties.getSubscribersBatchSize()) {
            kafkaPostProducer.sendEventToTopic(new FeedPostEvent(postId, authorId, publishedAt, subscribersIds), topicName);
            log.info("Sent FeedPostEvent for postId {} with subscribers {}", postId, subscribersIds);
        } else {
            List<List<Long>> batches = ListUtils.partition(subscribersIds, properties.getSubscribersBatchSize());

            AtomicInteger batchNumber = new AtomicInteger();
            batches.forEach(batch -> {
                FeedPostEvent event = new FeedPostEvent(postId, authorId, publishedAt, batch);
                String messageKey = postId + "-" + batchNumber;
                kafkaPostProducer.sendEventToTopic(event, messageKey, topicName);
                log.info("Sent FeedPostEvent for postId {} batch {} with subscribers {}", postId, batchNumber, batch);
                batchNumber.getAndIncrement();
            });
        }
    }

    //@Async()
    @Async("taskExecutor")
    public void createAndSendFeedPostDeletedEvent(long postId) {
        kafkaPostDeleteProducer.sendEvent(new FeedPostDeleteEvent(postId));
    }
}