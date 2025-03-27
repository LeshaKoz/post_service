package faang.school.postservice.broker.producer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@SpringBootTest
@Testcontainers
class KafkaProducerServiceTest {

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Test
    @DisplayName("Test sending message to kafka")
    void testSendMessage() {
        String topic = "test";
        String message = "Hello";
        kafkaProducerService.sendMessage(topic, message);

        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
            SendResult<String, String> result = kafkaTemplate.send(topic, message).get();
            assertNotNull(result.getRecordMetadata());
        });
    }
}