package faang.school.postservice.scheduler;

import faang.school.postservice.service.PostService;
import faang.school.postservice.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SchedulerTest {

    @Mock
    private UserService userService;

    @Mock
    private PostService postService;

    @InjectMocks
    private Scheduler scheduler;

    @Test
    void correctSuccessAllUnpublishedPostsIfUserExists() {
        when(userService.isUserExistsInContext()).thenReturn(true);

        scheduler.correctAllUnpublishedPosts();

        verify(userService).isUserExistsInContext();
        verify(postService).correctAllUnpublishedPosts();
    }

    @Test
    void correctAllUnpublishedPostsIfUserNotExists() {
        when(userService.isUserExistsInContext()).thenReturn(false);

        scheduler.correctAllUnpublishedPosts();

        verify(userService).isUserExistsInContext();
        verify(postService, times(0)).correctAllUnpublishedPosts();
    }
}