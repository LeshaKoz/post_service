package faang.school.postservice.consumer;

import faang.school.postservice.event.post.PostViewEvent;
import faang.school.postservice.service.cache.RedisCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaPostViewConsumer extends AbstractKafkaConsumer<PostViewEvent> {

    private final RedisCacheService redisCacheService;

    @Override
    @KafkaListener(
            topics = "${spring.kafka.topics.PostViewEvent.name}",
            groupId = "${spring.kafka.consumer.group-id}")
    protected void processEvent(PostViewEvent event) {
        redisCacheService.addPostView(event.getPostId());
    }
}
