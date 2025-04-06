package faang.school.postservice.consumer;

import faang.school.postservice.event.like.LikeEvent;
import faang.school.postservice.service.cache.RedisCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaLikeConsumer extends AbstractKafkaConsumer<LikeEvent> {

    private final RedisCacheService redisCacheService;

    @Override
    @KafkaListener(
            topics = "${spring.kafka.topics.LikeEvent.name}",
            groupId = "${spring.kafka.consumer.group-id}")
    protected void processEvent(LikeEvent event) {
        redisCacheService.addLikeToPost(event.postId(),
                event.likeId());
    }
}
