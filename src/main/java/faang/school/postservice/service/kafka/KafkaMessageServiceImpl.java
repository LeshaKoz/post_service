package faang.school.postservice.service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.enums.KafkaStatus;
import faang.school.postservice.kafka.KafkaProducer;
import faang.school.postservice.model.KafkaMessage;
import faang.school.postservice.repository.KafkaMessageRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaMessageServiceImpl implements KafkaMessageService {

    private final KafkaMessageRepository kafkaMessageRepository;
    private final KafkaProducer kafkaProducer;
    private final ObjectMapper objectMapper;

    @Value("${kafka.message.send.max-attempts}")
    private int maxAttempts;

    @Transactional
    @Override
    public void sendMessage(String topic, Object message) {
        String messageToSend = mapObjectToString(message);
        try {
            kafkaProducer.produce(topic, messageToSend);
            log.info("Message sent to topic: {}", topic);
        } catch (Exception e) {
            log.warn("Failed message send to topic: {}. Message added to queue", topic);
            kafkaMessageRepository.save(buildKafkaMessage(topic, messageToSend));
        }
    }

    @Transactional
    @Override
    public void sendMessages() {
        List<KafkaMessage> kafkaMessages = kafkaMessageRepository.findByStatusOrderByCreatedAt(KafkaStatus.PENDING);
        if (kafkaMessages.isEmpty()) {
            log.info("There is not pending message to send to kafka");
            return;
        }
        for (KafkaMessage message : kafkaMessages) {
            try {
                kafkaProducer.produce(message.getTopic(), message.getMessage());
                kafkaMessageRepository.deleteById(message.getId());
            } catch (Exception e) {
                log.error("Failed send to kafka message with id: {}", message.getId());
                if (message.getAttempts() > maxAttempts) {
                    kafkaMessageRepository.updateStatus(message.getId(), KafkaStatus.FAILED);
                    log.error("Message with id = {} marked as FAILED after max attempts", message.getId());
                } else {
                    kafkaMessageRepository.updateStatusAndIncrementAttempts(message.getId(), KafkaStatus.PENDING);
                    log.warn("Message with id = {} didn't send. Retrying...", message.getId());
                }
            }
        }
    }

    private String mapObjectToString(Object message) {
        try {
            return objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private KafkaMessage buildKafkaMessage(String topic, String messageToSend) {
        return KafkaMessage.builder()
                .topic(topic)
                .message(messageToSend)
                .status(KafkaStatus.PENDING)
                .attempts(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
