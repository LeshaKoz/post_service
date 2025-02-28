package faang.school.postservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ScheduledPostPublisherTest {
    @Mock
    private PostService postService;

    @InjectMocks
    private ScheduledPostPublisher scheduledPostPublisher;

    @Test
    void testPostPublisherSchedule() {
        scheduledPostPublisher.postPublisherSchedule();
        verify(postService, times(1)).publishScheduledPosts();
    }
}