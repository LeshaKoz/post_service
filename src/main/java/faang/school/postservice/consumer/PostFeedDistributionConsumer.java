package faang.school.postservice.consumer;

import faang.school.postservice.event.PostDistributionEvent;
import faang.school.postservice.service.NewsFeedService;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostFeedDistributionConsumer {
    private final PostService postService;
    private final NewsFeedService newsFeedService;

    @KafkaListener(topics = "${spring.kafka.topics.news-feed-subs.name}")
    private void consume(PostDistributionEvent event) {
        newsFeedService.addPostForNewsFeed(
                postService.getPostById(event.getPostId()), event.getFollowersIds()
        );
    }
}
