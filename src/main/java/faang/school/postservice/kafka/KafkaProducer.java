package faang.school.postservice.kafka;

public interface KafkaProducer {

    void produce(String topic, String message);
}