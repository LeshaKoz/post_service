package faang.school.postservice.broker;

import faang.school.postservice.dto.like.LikePostEvent;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducerLikeService {
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${spring.kafka.like_post_event_topic_name}")
    private String likePostEventTopicName;

    public void sendLikePostEvent(LikePostEvent event) {
        JSONObject json = new JSONObject(event);
        kafkaTemplate.send(likePostEventTopicName, json.toString());
    }
}
