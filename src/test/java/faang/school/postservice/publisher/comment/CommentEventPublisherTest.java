package faang.school.postservice.publisher.comment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.redis.Channels;
import faang.school.postservice.dto.comment.CommentEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import java.time.LocalDateTime;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class CommentEventPublisherTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private Channels channels;

    @InjectMocks
    private CommentEventPublisher commentEventPublisher;

    private CommentEvent commentEvent;
    private final String commentChannel = "comment-channel";
    private final String eventJson = "{\"postAuthorId\":1,\"commentAuthorId\":2,\"postId\":3,\"commentId\":4,\"commentedAt\":\"2023-10-01T12:00:00\"}";

    @BeforeEach
    void setUp() {
        commentEvent = CommentEvent.builder()
                .postAuthorId(1L)
                .commentAuthorId(2L)
                .postId(3L)
                .commentId(4L)
                .commentedAt(LocalDateTime.of(2023, 10, 1, 12, 0))
                .build();
    }

    @Test
    void testPublishSuccess() throws JsonProcessingException {
        when(channels.getCommentChannel()).thenReturn(commentChannel);
        when(objectMapper.writeValueAsString(commentEvent)).thenReturn(eventJson);

        commentEventPublisher.publish(commentEvent);

        verify(channels, times(1)).getCommentChannel();
        verify(objectMapper, times(1)).writeValueAsString(commentEvent);
        verify(redisTemplate, times(1)).convertAndSend(commentChannel, eventJson);
    }

    @Test
    void testPublishJsonProcessingException() throws JsonProcessingException {
        when(channels.getCommentChannel()).thenReturn(commentChannel);
        when(objectMapper.writeValueAsString(commentEvent)).thenThrow(JsonProcessingException.class);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> commentEventPublisher.publish(commentEvent));

        assertEquals("com.fasterxml.jackson.core.JsonProcessingException: N/A", exception.getMessage());
        verify(channels, times(1)).getCommentChannel();
        verify(objectMapper, times(1)).writeValueAsString(commentEvent);
        verify(redisTemplate, never()).convertAndSend(anyString(), anyString());
    }

    @Test
    void testHandleEventSuccess() throws JsonProcessingException {
        when(channels.getCommentChannel()).thenReturn(commentChannel);
        when(objectMapper.writeValueAsString(commentEvent)).thenReturn(eventJson);

        commentEventPublisher.publish(commentEvent);

        verify(channels, times(1)).getCommentChannel();
        verify(objectMapper, times(1)).writeValueAsString(commentEvent);
        verify(redisTemplate, times(1)).convertAndSend(commentChannel, eventJson);
    }
}