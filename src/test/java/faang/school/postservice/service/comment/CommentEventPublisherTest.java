package faang.school.postservice.service.comment;

import faang.school.postservice.dto.event.CommentEventDto;
import faang.school.postservice.mapper.comment.CommentEventMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.service.feed.AsyncAddAuthorCommentService;
import faang.school.postservice.service.kafka.KafkaCommentEventPublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KafkaCommentEventPublisherTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Mock
    private CommentEventMapper commentEventMapper; // Исправлено!

    @Mock
    private AsyncAddAuthorCommentService asyncAddAuthorCommentService;

    @InjectMocks
    private KafkaCommentEventPublisher publisher;

    @Test
    void testPublishCommentEvent() {
        String expectedTopic = "comments-topic";
        Comment comment = new Comment();
        comment.setId(123L);
        CommentEventDto dto = new CommentEventDto();

        ReflectionTestUtils.setField(publisher, "topic", expectedTopic);

        when(commentEventMapper.toDto(comment)).thenReturn(dto);

        publisher.publishCommentEvent(comment);

        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<CommentEventDto> valueCaptor = ArgumentCaptor.forClass(CommentEventDto.class);

        verify(kafkaTemplate).send(
                topicCaptor.capture(),
                keyCaptor.capture(),
                valueCaptor.capture()
        );

        assertEquals(expectedTopic, topicCaptor.getValue());
        assertEquals("123", keyCaptor.getValue());
        assertSame(dto, valueCaptor.getValue());
        verify(commentEventMapper, times(1)).toDto(comment); // Исправлено!
        verify(asyncAddAuthorCommentService).addAuthorByCommentToCashAsync(comment);
    }
}