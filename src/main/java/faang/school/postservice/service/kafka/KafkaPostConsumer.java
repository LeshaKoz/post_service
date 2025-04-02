package faang.school.postservice.service.kafka;

import faang.school.postservice.dto.post.PostCreatedEvent;
import faang.school.postservice.repository.PostCacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class KafkaPostConsumer {
    private final PostCacheRepository repository;

    @KafkaListener(topics = "${kafka.topic.post}",
            groupId = "${kafka.consumer.group-id}",
            containerFactory = "postEventConcurrentKafkaFactory")
    public void listen(PostCreatedEvent dto) {

    }
}

