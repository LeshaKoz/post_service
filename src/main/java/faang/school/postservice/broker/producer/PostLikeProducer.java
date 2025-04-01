package faang.school.postservice.broker.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.kafka.CustomKafkaProperties;
import faang.school.postservice.dto.post.PostLikeEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PostLikeProducer extends KafkaProducerService{
    private final CustomKafkaProperties customKafkaProperties;


    public PostLikeProducer(KafkaTemplate<String, PostLikeEvent> kafkaTemplate,
                            ObjectMapper objectMapper,
                            CustomKafkaProperties customKafkaProperties,
                            @Value("${spring.kafka.topic.post-likes-topic}") String topic) {
        super(kafkaTemplate, objectMapper, topic);
        this.customKafkaProperties = customKafkaProperties;
    }

    @Async("asyncTaskExecutor")
    public void produceLikePostEventAsync(long postId) {
        produceLikePostEvent(postId);
    }


    public void produceLikePostEvent(long postId) {

        PostLikeEvent postLikeEvent = PostLikeEvent.builder()
                .postId(postId)
                .build();

        super.sendMessage(postLikeEvent);
        log.info("Sending PostLikeEvent to message broker. Post : {}", postId);
    }
}
