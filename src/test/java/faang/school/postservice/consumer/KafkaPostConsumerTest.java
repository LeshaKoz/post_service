package faang.school.postservice.consumer;

import faang.school.postservice.event.PostEvent;
import faang.school.postservice.exception.InvalidPostEventException;
import faang.school.postservice.exception.SubscriberProcessingException;
import faang.school.postservice.service.FeedService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class KafkaPostConsumerTest {

    private static final long AUTHOR_ID = 1L;
    private static final long POST_ID = 100L;
    private static final List<Long> SUBSCRIBER_IDS = Arrays.asList(1L, 2L, 3L);
    private static final String INVALID_POST_EVENT_MESSAGE = "Ивент или ID подписчика не может быть null";
    private static final String SUBSCRIBER_PROCESSING_ERROR_MESSAGE = "Не удалось обработать подписчиков: ";

    @Mock
    private FeedService feedService;

    @InjectMocks
    private KafkaPostConsumer kafkaPostConsumer;

    private PostEvent validPostEvent;

    @BeforeEach
    void setUp() {
        validPostEvent = new PostEvent(
                AUTHOR_ID,
                POST_ID,
                SUBSCRIBER_IDS,
                LocalDateTime.now()
        );
    }

    @Test
    void listenTest_allSubscribersProcessedSuccessfully() {
        kafkaPostConsumer.listen(validPostEvent);

        verify(feedService, times(SUBSCRIBER_IDS.size())).addPostToFeed(anyLong(), eq(validPostEvent));
    }

    @Test
    void listenTest_partialFailure() {
        Long failedSubscriberId = SUBSCRIBER_IDS.get(2);
        doNothing().when(feedService).addPostToFeed(eq(SUBSCRIBER_IDS.get(0)), eq(validPostEvent));
        doNothing().when(feedService).addPostToFeed(eq(SUBSCRIBER_IDS.get(1)), eq(validPostEvent));
        doThrow(new RuntimeException("Test exception")).when(feedService)
                .addPostToFeed(eq(failedSubscriberId), eq(validPostEvent));

        SubscriberProcessingException exception = assertThrows(
                SubscriberProcessingException.class,
                () -> kafkaPostConsumer.listen(validPostEvent)
        );

        String expectedMessage = SUBSCRIBER_PROCESSING_ERROR_MESSAGE + "[" + failedSubscriberId + "]";
        assertEquals(expectedMessage, exception.getMessage());

    }

    @Test
    void listenTest_postEventIsInvalid() {
        InvalidPostEventException exception = assertThrows(
                InvalidPostEventException.class,
                () -> kafkaPostConsumer.listen(null)
        );

        assertEquals(INVALID_POST_EVENT_MESSAGE, exception.getMessage());
    }

    @Test
    void testListen_nullSubscribers() {
        PostEvent postEventWithNullSubscribers = new PostEvent(
                AUTHOR_ID,
                POST_ID,
                null,
                LocalDateTime.now()
        );

        InvalidPostEventException exception = assertThrows(InvalidPostEventException.class,
                () -> kafkaPostConsumer.listen(postEventWithNullSubscribers)
        );

        assertEquals(INVALID_POST_EVENT_MESSAGE, exception.getMessage());
        verify(feedService, never()).addPostToFeed(anyLong(), any(PostEvent.class));
    }

    @Test
    void testListen_allSubscribersFail() {
        doThrow(new RuntimeException("Ошибка обработки")).when(feedService)
                .addPostToFeed(anyLong(), any(PostEvent.class));

        SubscriberProcessingException exception = assertThrows(SubscriberProcessingException.class,
                () -> kafkaPostConsumer.listen(validPostEvent)
        );

        assertEquals(SUBSCRIBER_PROCESSING_ERROR_MESSAGE + SUBSCRIBER_IDS, exception.getMessage());
        verify(feedService, times(SUBSCRIBER_IDS.size())).addPostToFeed(anyLong(), eq(validPostEvent));
    }

    @Test
    void testListen_partialSubscriberFailure() {
        Long failedSubscriberId = SUBSCRIBER_IDS.get(2);
        doNothing().when(feedService).addPostToFeed(eq(SUBSCRIBER_IDS.get(0)), any(PostEvent.class));
        doNothing().when(feedService).addPostToFeed(eq(SUBSCRIBER_IDS.get(1)), any(PostEvent.class));
        doThrow(new RuntimeException("Ошибка обработки")).when(feedService)
                .addPostToFeed(eq(failedSubscriberId), any(PostEvent.class));

        SubscriberProcessingException exception = assertThrows(SubscriberProcessingException.class,
                () -> kafkaPostConsumer.listen(validPostEvent)
        );

        String expectedMessage = SUBSCRIBER_PROCESSING_ERROR_MESSAGE + "[" + failedSubscriberId + "]";
        assertEquals(expectedMessage, exception.getMessage());
        verify(feedService, times(SUBSCRIBER_IDS.size())).addPostToFeed(anyLong(), eq(validPostEvent));
    }
}
