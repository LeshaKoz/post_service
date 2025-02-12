package faang.school.postservice.service.like;

import faang.school.postservice.event.LikeEvent;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LikeEventPublisherTest {
    @Mock
    private KafkaTemplate<String, LikeEvent> kafkaTemplate;

    @InjectMocks
    private LikeEventPublisher likeEventPublisher;

    @Test
    void publish() {
        CompletableFuture future = mock(CompletableFuture.class);
        SendResult<String, LikeEvent> sendResult = mock(SendResult.class);
        ProducerRecord<String, LikeEvent> producerRecord = mock(ProducerRecord.class);

        LikeEvent event = LikeEvent.builder()
                .authorId(3L)
                .userId(2L)
                .postId(5L)
                .likeTime(LocalDateTime.of(2025, 2, 11, 14, 45))
                .build();

        when(kafkaTemplate.send(anyString(), anyString(), any())).thenReturn(future);
        when(future.join()).thenReturn(sendResult);
        when(sendResult.getProducerRecord()).thenReturn(producerRecord);
        when(producerRecord.topic()).thenReturn("likes");
        when(producerRecord.value()).thenReturn(event);

        SendResult<String, Object> result = likeEventPublisher.publish(event);

        assertEquals(event, result.getProducerRecord().value());
        assertEquals("likes", result.getProducerRecord().topic());

        verify(kafkaTemplate, times(1)).send(anyString(), anyString(), any());
        verify(future, times(1)).join();
        verify(sendResult, times(2)).getProducerRecord();
        verify(producerRecord, times(1)).topic();
        verify(producerRecord, times(1)).value();
    }
}