package faang.school.postservice.publisher.kafka;

import faang.school.postservice.dto.post.PostCreatedEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaPostProducer extends AbstractEventProducer<PostCreatedEvent> {

    public KafkaPostProducer(KafkaTemplate<String, Object> kafkaTemplate, NewTopic post) {
        super(kafkaTemplate, post);
    }

    @Override
    public void sendEvent(PostCreatedEvent event) {
        super.sendEvent(event);
    }
}
