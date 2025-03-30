package faang.school.postservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.event.PostCreatedEvent;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class KafkaPostProducerTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    PostRepository postRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private KafkaPostProducer kafkaPostProducer;

    @Captor
    private ArgumentCaptor<String> messageCaptor;
    @Test
    void shouldPublishPostCreationEvent() throws JsonProcessingException {
        Long postId = 1L;
        Long authorId = 2L;
        Post post = new Post();
        post.setId(postId);
        post.setAuthorId(authorId);
        kafkaPostProducer.setTopic("publish_post_topic");
        List<Long> subscriberIds = List.of(3L, 4L, 5L);
        when(postRepository.findAllAuthorSubscribers(authorId)).thenReturn(subscriberIds);

        PostCreatedEvent event = new PostCreatedEvent(postId, authorId, subscriberIds);
        when(objectMapper.writeValueAsString(any(PostCreatedEvent.class))).thenReturn("{\"json\":\"content\"}");

        kafkaPostProducer.publishPostCreationEvent(post);

        verify(kafkaTemplate).send(eq("publish_post_topic"), messageCaptor.capture());
        verify(objectMapper).writeValueAsString(any(PostCreatedEvent.class));
        verify(postRepository).findAllAuthorSubscribers(authorId);
    }

    @Test
    void shouldHandleJsonProcessingException() throws JsonProcessingException {
        Post post = new Post();
        post.setId(1L);
        post.setAuthorId(2L);
        kafkaPostProducer.setTopic("publish_post_topic");
        List<Long> subscriberIds = List.of(3L, 4L, 5L);

        when(postRepository.findAllAuthorSubscribers(post.getAuthorId())).thenReturn(subscriberIds);

        when(objectMapper.writeValueAsString(any(PostCreatedEvent.class)))
                .thenThrow(new JsonProcessingException("Error processing JSON") {});

        assertThrows(RuntimeException.class,
                () -> kafkaPostProducer.publishPostCreationEvent(post));

        verify(kafkaTemplate, never()).send(anyString(), anyString());
    }
}