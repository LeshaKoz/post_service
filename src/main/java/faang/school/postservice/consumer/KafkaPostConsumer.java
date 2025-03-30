package faang.school.postservice.consumer;

import faang.school.postservice.event.post.PostEvent;
import faang.school.postservice.service.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaPostConsumer extends AbstractKafkaConsumer<PostEvent> {

    private final FeedService feedService;

    @Override
    @KafkaListener(
            topics = "${spring.kafka.topics.PostEvent.name}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void processEvent(PostEvent event) {
        feedService.addPostToAuthorSubscribers(event.getPostId(),
                event.getSubscribersId());
    }
}
