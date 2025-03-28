package faang.school.postservice.service.feed;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.FeedProperties;
import faang.school.postservice.dto.feed.FeedPostDeleteEvent;
import faang.school.postservice.dto.feed.FeedPostEvent;
import faang.school.postservice.producer.KafkaPostDeleteProducer;
import faang.school.postservice.producer.KafkaPostProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

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
        List<Long> subscribersIds = userServiceClient.getFollowerIdsByFolloweeId(authorId);
        double score = publishedAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

        if (subscribersIds == null || subscribersIds.isEmpty()) {
            log.info("Author {} has no subscribers or failed to retrieve subscribers. No events will be sent.", authorId);
        } else if (subscribersIds.size() <= properties.getSubscribersBatchSize()) {
            kafkaPostProducer.sendEventToTopic(new FeedPostEvent(postId, authorId, publishedAt, subscribersIds), topicName);
            log.info("Sent FeedPostEvent for postId {} with {} subscribers", postId, subscribersIds.size());
        } else {
            List<List<Long>> batches = partitionList(subscribersIds, properties.getSubscribersBatchSize());

            int batchNumber = 0;
            for (List<Long> batch : batches) {
                FeedPostEvent event = new FeedPostEvent(postId, authorId, publishedAt, batch);

                String messageKey = postId + "-" + batchNumber;

                kafkaPostProducer.sendEventToTopic(event, messageKey, topicName);

                log.info("Sent FeedPostEvent for postId {} batch {} with {} subscribers", postId, batchNumber, batch.size());

                batchNumber++;
            }
        }
    }

    private <T> List<List<T>> partitionList(List<T> list, int batchSize) {
        int totalSize = list.size();
        List<List<T>> partitions = new ArrayList<>();
        for (int i = 0; i < totalSize; i += batchSize) {
            partitions.add(list.subList(i, Math.min(i + batchSize, totalSize)));
        }
        return partitions;
    }

    //@Async("feedExecutor")
    public void createAndSendFeedPostDeletedEvent(long postId) {
        kafkaPostDeleteProducer.sendEvent(new FeedPostDeleteEvent(postId));
    }
}