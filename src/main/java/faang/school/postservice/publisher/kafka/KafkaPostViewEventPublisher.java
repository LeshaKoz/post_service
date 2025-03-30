package faang.school.postservice.publisher.kafka;

import faang.school.postservice.event.kafka.AbstractKafkaEventDto;
import faang.school.postservice.event.kafka.KafkaPostViewEventDto;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;

public class KafkaPostViewEventPublisher extends AbstractEventPublisher {
    @Value(value = "${spring.data.kafka.keys.post-view}")
    private String postViewKey;

    public KafkaPostViewEventPublisher(KafkaTemplate<String, AbstractKafkaEventDto> kafkaTemplate, @Qualifier("postViewTopic") NewTopic topic) {
        super(kafkaTemplate, topic);
    }

    public void sendPostViewEvent(KafkaPostViewEventDto eventDto) {
        sendEvent(eventDto, postViewKey);
    }
}
