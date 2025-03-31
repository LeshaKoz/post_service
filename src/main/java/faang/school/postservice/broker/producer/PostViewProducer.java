package faang.school.postservice.broker.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.kafka.CustomKafkaProperties;
import faang.school.postservice.dto.post.PostViewEvent;
import faang.school.postservice.mapper.user.UserDtoAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PostViewProducer extends KafkaProducerService{

    private final CustomKafkaProperties customKafkaProperties;

    public PostViewProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper,
                            CustomKafkaProperties customKafkaProperties, UserDtoAdapter userDtoAdapter) {
        super(kafkaTemplate, objectMapper);
        this.customKafkaProperties = customKafkaProperties;
    }

    @Async("asyncTaskExecutor")
    public void produceViewPostEventAsync(long postId, Long visitorId) {
        produceViewPostEvent(postId, visitorId);
    }

    public void produceViewPostEvent(long postId, Long visitorId) {

        PostViewEvent postViewEvent = PostViewEvent.builder()
                .postId(postId)
                .userId(visitorId)
                .build();

        super.sendPostMessage(customKafkaProperties.topic().postViewsTopic(), postViewEvent);
        log.info("Sending PostViewEvent to message broker. Post : {}", postId);
    }
}
