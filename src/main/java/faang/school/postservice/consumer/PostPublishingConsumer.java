package faang.school.postservice.consumer;

import faang.school.postservice.event.PostDistributionEvent;
import faang.school.postservice.event.PostEvent;
import faang.school.postservice.publisher.NewsFeedPublicationProducer;
import faang.school.postservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutorService;

import static java.util.concurrent.CompletableFuture.runAsync;

@Service
@RequiredArgsConstructor
public class PostPublishingConsumer {
    private final UserService userService;
    private final ExecutorService executorService;
    private final NewsFeedPublicationProducer newsFeedPublicationProducer;

    @Value("${newsFeed.followersBatchSize:3}")
    private int followersBatchSize;

    @KafkaListener(topics = "${spring.kafka.topics.post.name}")
    public void consumePostForNewsFeed(PostEvent event) {
        int followersCount = userService.getUserFollowersCount(event.getUserId());
        long postId = event.getId();
        long userId = event.getUserId();
        for (int i = 0; i < followersCount; i += followersBatchSize) {
            int finalI = i;
            runAsync(() -> getFollowersAndPublishEvent(userId, postId, finalI), executorService);
        }
    }

    private void getFollowersAndPublishEvent(long userId, long postId, int i) {
        List<Long> followersIds = userService.getUserFollowers(
                userId,
                i / followersBatchSize,
                followersBatchSize
        );
        var event = PostDistributionEvent.builder()
                .postId(postId)
                .followersIds(followersIds)
                .build();
        newsFeedPublicationProducer.publish(event);
    }
}
