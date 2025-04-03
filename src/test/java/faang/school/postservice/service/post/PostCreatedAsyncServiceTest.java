package faang.school.postservice.service.post;

import faang.school.postservice.dto.post.PostCreatedEvent;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostCreatedAsyncServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private PostCreatedAsyncService asyncService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(asyncService, "topicName", "posts");
        ReflectionTestUtils.setField(asyncService, "subscribersChunkSize", 10);
    }

    @Test
    void shouldFetchFollowersAndSendKafkaEvent() {
        Post post = new Post();
        post.setId(123L);
        post.setAuthorId(1L);

        List<Long> followerIds = List.of(10L, 20L, 30L);
        when(postRepository.findFollowerIdsByFolloweeId(1L)).thenReturn(followerIds);

        asyncService.processPostCreated(post);

        PostCreatedEvent expectedEvent = new PostCreatedEvent(123L, 1L, followerIds);
        verify(postRepository).findFollowerIdsByFolloweeId(1L);
        verify(kafkaTemplate).send("posts", "123", expectedEvent);
    }
}
