package faang.school.postservice.service.kafka;

public interface KafkaMessageService {

    void sendMessage(String topic, Object message);

    void sendMessages();
}
