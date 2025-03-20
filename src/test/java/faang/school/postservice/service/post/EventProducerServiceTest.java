package faang.school.postservice.service.post;

import faang.school.postservice.event.PostViewEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class EventProducerServiceTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    private EventProducerService eventProducerService;

    private final String testTopic = "test-post-view-topic";

    @BeforeEach
    public void setUp() {
        eventProducerService = new EventProducerService(kafkaTemplate);
        ReflectionTestUtils.setField(eventProducerService, "postViewTopic", testTopic);
    }

    @Test
    public void publish_shouldSendMessageToKafka() {
        PostViewEvent event = PostViewEvent.builder()
                .postId(1L)
                .authorId(2L)
                .userId(3L)
                .build();

        eventProducerService.publish(event);

        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object> valueCaptor = ArgumentCaptor.forClass(Object.class);

        verify(kafkaTemplate).send(topicCaptor.capture(), keyCaptor.capture(), valueCaptor.capture());

        assertEquals(testTopic, topicCaptor.getValue());

        assertEquals(event, valueCaptor.getValue());

        assertNotNull(keyCaptor.getValue());
    }
}