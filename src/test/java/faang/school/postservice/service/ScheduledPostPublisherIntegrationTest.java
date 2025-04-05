package faang.school.postservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.repository.PostCacheRepository;
import faang.school.postservice.util.BaseContextTest;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ActiveProfiles("test")
@RequiredArgsConstructor
@ExtendWith(OutputCaptureExtension.class)
@SpringBootTest(
        properties = {
                "spring.redis.host=localhost",
                "spring.redis.port=6379",
                "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration"
        },

        classes = {ScheduledPostPublisher.class, PostService.class}
)
public class ScheduledPostPublisherIntegrationTest extends BaseContextTest {

    @Autowired
    private ScheduledPostPublisher scheduledPostPublisher;

    @MockBean
    private PostService postService;

    @MockBean
    private PostCacheRepository postCacheRepository;

    @MockBean
    private ObjectMapper objectMapper;

    @Test
    void testPostPublisherSchedule(CapturedOutput output) {
        doNothing().when(postService).publishScheduledPosts();
        scheduledPostPublisher.postPublisherSchedule();
        verify(postService, times(1)).publishScheduledPosts();

        assertTrue(output.getOut().contains("Publish scheduler started processing"));
        assertTrue(output.getOut().contains("Publish scheduler finished processing"));
    }
}
