package faang.school.postservice.config.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.CreatePostEvent;
import faang.school.postservice.model.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaFeedPostProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final UserServiceClient userServiceClient;
    private final ObjectMapper objectMapper;

    @Value("${spring.kafka.producer.news-feed.post-followers-batch-max-size}")
    private int postFollowersBatchMaxSize;

    @Value("${spring.kafka.topics.post-create-event}")
    private String topicCreatePostName;

    @Async("kafkaSendEventThreadPool")
    public void sendCreatePostEvent(Post post) {
        log.info("Publishing create post event");
        CreatePostEvent event;
        List<Long> followersIds = userServiceClient.getFollowersIdsByUserId(post.getAuthorId());

        while (followersIds.size() > postFollowersBatchMaxSize) {
            List<Long> currentFollowersBatch = followersIds.stream().limit(postFollowersBatchMaxSize).toList();
            followersIds = followersIds.stream().filter(id -> !currentFollowersBatch.contains(id)).toList();
            event = new CreatePostEvent(post.getId(), currentFollowersBatch);
            send(topicCreatePostName, event.postId(), event);
        }

        if (followersIds.size() > 0) {
            event = new CreatePostEvent(post.getId(), followersIds);
            send(topicCreatePostName, event.postId(), event);
        }
    }

    private void send(String topicName, Object key, Object data) {
        try {
            kafkaTemplate.send(topicName, objectMapper.writeValueAsString(key), objectMapper.writeValueAsString(data));
        } catch (JsonProcessingException e) {
            log.error("couldn't convert object to json: " + e);
        }
    }
}
