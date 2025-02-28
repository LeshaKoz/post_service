package faang.school.postservice.service;

import faang.school.postservice.util.BaseContextTest;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
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
public class ScheduledPostPublisherIntegrationTest extends BaseContextTest {

    @Autowired
    private ScheduledPostPublisher scheduledPostPublisher;

    @MockBean
    private PostService postService;

    @Test
    void testPostPublisherSchedule(CapturedOutput output) {
        doNothing().when(postService).publishScheduledPosts();
        scheduledPostPublisher.postPublisherSchedule();
        verify(postService, times(1)).publishScheduledPosts();

        assertTrue(output.getOut().contains("Publish scheduler started processing"));
        assertTrue(output.getOut().contains("Publish scheduler finished processing"));
    }
}
