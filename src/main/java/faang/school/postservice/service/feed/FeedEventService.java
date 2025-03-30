package faang.school.postservice.service.feed;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.FeedProperties;
import faang.school.postservice.dto.feed.FeedPostDeleteEvent;
import faang.school.postservice.dto.feed.FeedPostEvent;
import faang.school.postservice.producer.KafkaPostDeleteProducer;
import faang.school.postservice.producer.KafkaPostProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.ZoneId;
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


    //@Async("feedExecutor")
    @Async
    public void createAndSendFeedPostEventForNewPost(Long postId, Long authorId, LocalDateTime publishedAt) {
        createAndSendFeedPostEvent(postId, authorId, publishedAt, properties.getPostTopic());
    }

    private void createAndSendFeedPostEvent(Long postId, Long authorId, LocalDateTime publishedAt, String topicName) {
        log.info("createAndSendFeedPostEvent postId {} authorId {} publishedAt {} topicName {}",
                postId, authorId, publishedAt, topicName);
        List<Long> subscribersIds = userServiceClient.getFollowerIdsByFolloweeId(authorId);
        log.info("subscribersIds {} ", subscribersIds);
        if (subscribersIds == null || subscribersIds.isEmpty()) {
            log.info("Author {} has no subscribers or failed to retrieve subscribers. No events will be sent.", authorId);
        } else if (subscribersIds.size() <= properties.getSubscribersBatchSize()) {
            kafkaPostProducer.sendEventToTopic(new FeedPostEvent(postId, authorId, publishedAt, subscribersIds), topicName);
            log.info("Sent FeedPostEvent for postId {} with subscribers {}", postId, subscribersIds);
        } else {
            //List<List<Long>> batches = partitionList(subscribersIds, properties.getSubscribersBatchSize());
            List<List<Long>> batches = ListUtils.partition(subscribersIds, properties.getSubscribersBatchSize());

            AtomicInteger batchNumber = new AtomicInteger();
            batches.forEach(batch -> {
                FeedPostEvent event = new FeedPostEvent(postId, authorId, publishedAt, batch);
                String messageKey = postId + "-" + batchNumber;
                kafkaPostProducer.sendEventToTopic(event, messageKey, topicName);
                log.info("Sent FeedPostEvent for postId {} batch {} with subscribers {}", postId, batchNumber, batch);
                batchNumber.getAndIncrement();
            });

            /*
            for (List<Long> batch : batches) {
                FeedPostEvent event = new FeedPostEvent(postId, authorId, publishedAt, batch);
                String messageKey = postId + "-" + batchNumber;
                kafkaPostProducer.sendEventToTopic(event, messageKey, topicName);
                log.info("Sent FeedPostEvent for postId {} batch {} with {} subscribers", postId, batchNumber, batch.size());
                batchNumber.getAndIncrement();
            }*/
        }
    }
/*
    private <T> List<List<T>> partitionList(List<T> list, int batchSize) {
        int totalSize = list.size();
        List<List<T>> partitions = new ArrayList<>();
        for (int i = 0; i < totalSize; i += batchSize) {
            partitions.add(list.subList(i, Math.min(i + batchSize, totalSize)));
        }
        return partitions;
    }
*/
    //@Async("feedExecutor")
    public void createAndSendFeedPostDeletedEvent(long postId) {
        kafkaPostDeleteProducer.sendEvent(new FeedPostDeleteEvent(postId));
    }
}