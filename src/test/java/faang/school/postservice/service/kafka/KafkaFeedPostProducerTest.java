package faang.school.postservice.service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.kafka.KafkaFeedPostProducer;
import faang.school.postservice.model.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class KafkaFeedPostProducerTest {
    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    public KafkaFeedPostProducer kafkaFeedPostProducer;

    private final String topicCreatePostName = "create-post-event-topic";
    private final int postFollowersBatchMaxSize = 5;

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(kafkaFeedPostProducer,
                "postFollowersBatchMaxSize", postFollowersBatchMaxSize);
        ReflectionTestUtils.setField(kafkaFeedPostProducer,
                "topicCreatePostName", topicCreatePostName);
    }

    @Test
    public void sendCreatePostEvent_3Iterations() throws JsonProcessingException {
        Long postId = 1L;
        long authorId = 1L;

        Post post = Post.builder()
                .id(postId)
                .authorId(authorId)
                .content("some content")
                .build();
        List<Long> followersIds = List.of(5L, 6L, 7L, 8L, 9L, 20L, 543L, 33L, 76L, 34L, 83L, 15L);

        when(userServiceClient.getFollowersIdsByUserId(authorId)).thenReturn(followersIds);
        when(objectMapper.writeValueAsString(any())).thenReturn("something");

        kafkaFeedPostProducer.sendCreatePostEvent(post);

        verify(kafkaTemplate, times(3)).send(any(String.class), any(String.class));
    }

    @Test
    public void sendCreatePostEvent_1Iteration() throws JsonProcessingException {
        Long postId = 1L;
        long authorId = 1L;

        Post post = Post.builder()
                .id(postId)
                .authorId(authorId)
                .content("some content")
                .build();
        List<Long> followersIds = List.of(5L, 6L, 7L, 8L, 9L);

        when(userServiceClient.getFollowersIdsByUserId(authorId)).thenReturn(followersIds);
        when(objectMapper.writeValueAsString(any())).thenReturn("something");

        kafkaFeedPostProducer.sendCreatePostEvent(post);

        verify(kafkaTemplate, times(1)).send(any(String.class), any(String.class));
    }

    @Test
    public void sendCreatePostEvent_emptyList() {
        Long postId = 1L;
        long authorId = 1L;

        Post post = Post.builder()
                .id(postId)
                .authorId(authorId)
                .content("some content")
                .build();
        List<Long> followersIds = List.of();

        when(userServiceClient.getFollowersIdsByUserId(authorId)).thenReturn(followersIds);

        kafkaFeedPostProducer.sendCreatePostEvent(post);

        verify(kafkaTemplate, never()).send(any(String.class), any(String.class));
    }
}
