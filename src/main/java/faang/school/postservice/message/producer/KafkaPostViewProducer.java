package faang.school.postservice.message.producer;


import faang.school.postservice.message.event.PostEvent;
import faang.school.postservice.message.event.PostViewEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaPostViewProducer {

    private final KafkaTemplate<String, PostViewEvent> kafkaTemplate;

    @Value("${spring.kafka.topic.view-topic.name}")
    private String postViewTopicName;

    public void publish(PostViewEvent postViewEvent) {
        log.info("Sending post view event {} into topic {}", postViewEvent, postViewTopicName);
        kafkaTemplate.send(postViewTopicName, postViewEvent);
    }
}
