package faang.school.postservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.event.PostCreatedEvent;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Setter
public class KafkaPostProducer {
    private final PostRepository postRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${spring.kafka.topics.publish-post-topic.name}")
    private String topic;

    public void publishPostCreationEvent(Post post) {
        List<Long> subscribers = postRepository.findAllAuthorSubscribers(post.getAuthorId());

        PostCreatedEvent event = new PostCreatedEvent(
                post.getId(),
                post.getAuthorId(),
                subscribers
        );

        try {
            String eventJson = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(topic, eventJson);
            log.info("Published post creation event: postId={}, authorId={}, subscribersCount={}",
                    post.getId(), post.getAuthorId(), subscribers.size());
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize post event: {}", e.getMessage());
            throw new RuntimeException("Failed to publish post creation event", e);
        }
    }
}
