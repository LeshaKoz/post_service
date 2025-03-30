package faang.school.postservice.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.Acknowledgment;

@Slf4j
public abstract class AbstractKafkaConsumer<T> {

    public void handleEvent(T event, Acknowledgment acknowledgment) {
        try {
            processEvent(event);
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("Ошибка обработки события: {}", event, e);
            throw e;
        }
    }

    protected abstract void processEvent(T event);
}
