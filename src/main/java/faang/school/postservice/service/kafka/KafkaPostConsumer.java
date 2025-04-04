package faang.school.postservice.service.kafka;

import faang.school.postservice.dto.post.PostCreatedEvent;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.FeedRepository;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaPostConsumer {
    private final FeedRepository feedRepository;
    private final PostService postService;

    @KafkaListener(topics = "${kafka.topic.post}",
            groupId = "${kafka.consumer.group-id}",
            containerFactory = "postEventConcurrentKafkaFactory")
    public void listen(PostCreatedEvent dto, Acknowledgment ack) {
        try {
            Post post = postService.getPost(dto.postId());
            List<Long> subscriberIds = dto.subscriberIds();

            subscriberIds.forEach(id -> feedRepository.addPostToFeed(id, post));

            ack.acknowledge();
        } catch (Exception e) {
            log.error("Error processing kafka message {}", e.getMessage());
        }
    }
}

