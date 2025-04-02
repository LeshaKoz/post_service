package faang.school.postservice.publisher.kafka;

import faang.school.postservice.event.kafka.AbstractKafkaEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractEventPublisher {
    private final KafkaTemplate<String, AbstractKafkaEventDto> kafkaTemplate;
    private final NewTopic topic;

    protected void sendEvent(AbstractKafkaEventDto eventDto, String eventKey) {
        try {
            CompletableFuture<SendResult<String, AbstractKafkaEventDto>> future = kafkaTemplate.send(
                    topic.name(),
                    eventKey,
                    eventDto
            );

            future.whenComplete((result, exception) -> {
                if (exception == null) {
                    log.info("Успешно отправлено событие {} в топик {} с ключом [{}]",
                            eventDto.getEventId(), topic.name(), eventKey);
                } else {
                    log.error("При отправки события {} произошла ошибка", eventDto, exception);
                }
            });

        } catch (Exception e) {
            log.error("Ошибка обработки события {}: {}", eventDto, e.getMessage(), e);
        }
    }
}
