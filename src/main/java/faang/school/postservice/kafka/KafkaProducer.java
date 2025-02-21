package faang.school.postservice.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface KafkaProducer<T> {

    void produce(T obj) throws JsonProcessingException;
}