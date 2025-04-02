package faang.school.postservice.listener;

import faang.school.postservice.event.kafka.KafkaPostViewEventDto;
import faang.school.postservice.service.feed.NewsFeedService;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaPostViewConsumer {

    private final NewsFeedService newsFeedService;
    private final PostService postService;

    @KafkaListener(topics = "${spring.data.kafka.topic.posts-views}", groupId = "${spring.data.kafka.group-id}")
    public void handle (KafkaPostViewEventDto postViewEventDto, Acknowledgment ack) {
        try {
            Long postViews = postService.incrementPostView(postViewEventDto.getPostId());
            newsFeedService.updatePostCountView(postViewEventDto.getPostId(), postViews);
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Ошибка обработки События в Kafka: {}", e.getMessage());
        }
    }
}
