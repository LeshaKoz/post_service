package faang.school.postservice.kafka.producer;

import faang.school.postservice.dto.feed.FeedDto;
import faang.school.postservice.dto.post.PostDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class KafkaEventProducer {

    @Value("${spring.kafka.topic-name.heat}")
    private String heatTopic;

    private final KafkaTemplate<Long, Object> kafkaTemplate;

    public void sendFeedHeatEvent(FeedDto event) {
        kafkaTemplate.send(heatTopic, event)
                .thenRun(() -> {
                })
                .exceptionally(ex -> {
                    throw new RuntimeException("Failed to send feed heat event", ex);
                });
    }

    public void sendPostHeatEvent(PostDto event) {
        kafkaTemplate.send(heatTopic, event)
                .thenRun(() -> {
                })
                .exceptionally(ex -> {
                    throw new RuntimeException("Failed to send post heat event", ex);
                });
    }
}