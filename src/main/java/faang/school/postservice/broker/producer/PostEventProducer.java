package faang.school.postservice.broker.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.kafka.KafkaProperties;
import faang.school.postservice.dto.post.PostPublicationEvent;
import faang.school.postservice.mapper.user.UserDtoAdapter;
import faang.school.postservice.model.Post;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class PostEventProducer extends KafkaProducerService{

    private final KafkaProperties kafkaProperties;

    private final UserDtoAdapter userDtoAdapter;

    public PostEventProducer(KafkaTemplate<String, String> kafkaTemplate,
                             KafkaProperties kafkaProperties,
                             ObjectMapper objectMapper,
                             UserDtoAdapter userDtoAdapter) {
        super(kafkaTemplate, objectMapper);
        this.kafkaProperties = kafkaProperties;
        this.userDtoAdapter = userDtoAdapter;
    }

    @Async("asyncTaskExecutor")
    public void producePublishPostEventAsync(Post post, List<Long> followersIds) {
        producePublishPostEvent(post, followersIds);
    }

    public void producePublishPostEvent(Post post, List<Long> followersIds) {
        Long userId = post.getAuthorId();

        PostPublicationEvent postPublicationEvent = PostPublicationEvent.builder()
                .userId(userId)
                .postId(post.getId())
                .followersIds(followersIds)
                .build();
        sendPostMessage(kafkaProperties.topic().postsTopic(), postPublicationEvent);
        log.info("Sending PublishPostEvent to message broker. Post : {}", post.getId());
    }

    private void sendPostMessage(String topic, PostPublicationEvent post) {
        try {
            sendMessage(topic, super.objectMapper.writeValueAsString(post));
        } catch (JsonProcessingException e) {
            log.error("Error serializing post message: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
