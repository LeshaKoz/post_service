package faang.school.postservice.consumer;

import faang.school.postservice.event.comment.CommentEvent;
import faang.school.postservice.service.cache.RedisCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaCommentConsumer extends AbstractKafkaConsumer<CommentEvent> {

    private final RedisCacheService redisCacheService;

    @Override
    @KafkaListener(
            topics = "${spring.kafka.topics.CommentEvent.name}",
            groupId = "${spring.kafka.consumer.group-id}")
    protected void processEvent(CommentEvent event) {

        redisCacheService.addCommentToPostCache(event.getPostId(), event.getComment());
    }
}
