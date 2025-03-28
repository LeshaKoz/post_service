package faang.school.postservice.broker.consumer;

import faang.school.postservice.config.kafka.KafkaProperties;
import faang.school.postservice.dto.post.PostPublicationEvent;
import faang.school.postservice.service.feed.FeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostEventConsumer {

    private final FeedService feedService;
    private final KafkaProperties kafkaProperties;

    @KafkaListener(topics = "posts", groupId = "newsfeed")
    public void consume(PostPublicationEvent postPublicationEvent) {

        feedService.processNewPost(postPublicationEvent.postId(), postPublicationEvent.followersIds());
        log.info("### User {} is published the post {}", postPublicationEvent.userId(), postPublicationEvent.postId());
    }
}
