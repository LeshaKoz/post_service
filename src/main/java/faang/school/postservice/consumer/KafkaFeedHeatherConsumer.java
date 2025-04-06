package faang.school.postservice.consumer;

import faang.school.postservice.event.feed.FeedEvent;
import faang.school.postservice.service.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class KafkaFeedHeatherConsumer extends AbstractKafkaConsumer<FeedEvent> {

    private final FeedService feedService;

    @Qualifier("caching")
    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Override
    @KafkaListener(
            topics = "${spring.kafka.topics.FeedEvent.name}",
            groupId = "${spring.kafka.consumer.group-id}")
    protected void processEvent(FeedEvent event) {
        event.getUserIds().forEach(userId ->
                CompletableFuture.runAsync(() ->
                        createFeedForOneUser(userId), threadPoolTaskExecutor));
    }

    private void createFeedForOneUser(long userId) {
        feedService.getUserFeed(userId, null);
    }
}
