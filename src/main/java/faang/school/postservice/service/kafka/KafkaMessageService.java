package faang.school.postservice.service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface KafkaMessageService {

    void saveMessage(String topic, Object message) throws JsonProcessingException;

    void sendMessages();
}
