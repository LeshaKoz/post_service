package faang.school.postservice.publisher.kafka;

import faang.school.postservice.config.kafka.KafkaTopicConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaCacheUserProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaTopicConfig kafkaTopicConfig;

    public void send(List<Long> usersIds) {
        kafkaTemplate.send(kafkaTopicConfig.cacheUserTopic().name(), usersIds);
        log.info("Sent event to cache {} users", usersIds.size());
    }
}
