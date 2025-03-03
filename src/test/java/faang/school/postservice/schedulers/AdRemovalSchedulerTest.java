package faang.school.postservice.schedulers;

import faang.school.postservice.service.AdsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AdRemovalSchedulerTest {

    @Mock
    private AdsService adsService;

    @InjectMocks
    private ScheduledExpiredAdRemover scheduledExpiredAdRemover;

    @Test
    public void testRemovePremium() {
        scheduledExpiredAdRemover.startRemovingAds();
        verify(adsService).deleteExpiredAds();
    }
}
