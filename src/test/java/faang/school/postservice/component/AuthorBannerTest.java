package faang.school.postservice.component;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import faang.school.postservice.service.UserModerationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
class AuthorBannerTest {
    @Mock
    private UserModerationService userModerationService;

    @InjectMocks
    private AuthorBanner authorBanner;

    @Test
    void test_runDailyTask(){
        authorBanner.runDailyTask();

        verify(userModerationService, times(1)).checkAndBanUsersWithUnverifiedPosts();
    }
}