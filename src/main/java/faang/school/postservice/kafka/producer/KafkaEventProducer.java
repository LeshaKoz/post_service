package faang.school.postservice.kafka.producer;

import faang.school.postservice.dto.feed.FeedDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.kafka.events.CommentEvent;
import faang.school.postservice.kafka.events.PostFollowersEvent;
import faang.school.postservice.kafka.events.PostLikeEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class KafkaEventProducer {

    @Value("${spring.kafka.topics.heat.name}")
    private String heatTopic;

    @Value("${spring.kafka.topic-name.posts}")
    private String postTopic;

    @Value("${spring.kafka.topic-name.likes}")
    private String likeTopic;

    @Value("${spring.kafka.topic-name.comments}")
    private String commentTopic;

    private final KafkaTemplate<Long, Object> kafkaTemplate;

    public void sendFeedHeatEvent(FeedDto event) {
        kafkaTemplate.send(heatTopic, event)
                .thenRun(() -> {})
                .exceptionally(ex -> {
                    throw new RuntimeException("Failed to send feed heat event", ex);
                });
    }

    public void sendPostHeatEvent(PostDto event) {
        kafkaTemplate.send(heatTopic, event)
                .thenRun(() -> {})
                .exceptionally(ex -> {
                    throw new RuntimeException("Failed to send post heat event", ex);
                });
    }

    public void sendPostFollowersEvent(PostFollowersEvent event) {
        kafkaTemplate.send(postTopic, event);
    }

    public void sendLikeEvent(PostLikeEvent event) {
        kafkaTemplate.send(likeTopic, event);
    }

    public void sendCommentEvent(CommentEvent event) {
        kafkaTemplate.send(commentTopic, event);
    }
}