package faang.school.postservice.service.broker;

import faang.school.postservice.model.event.CommentCreateEvent;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducerCommentService {
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${spring.kafka.comment_create_event_topic_name}")
    private String commentCreateEventTopicName;

    public void sendCommentCreateEvent(CommentCreateEvent event) {
        JSONObject jsonObject = new JSONObject(event);
        kafkaTemplate.send(commentCreateEventTopicName, jsonObject.toString());
    }
}
