package faang.school.postservice.scheduler;

import faang.school.postservice.service.AdService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ScheduleExpiredAdRemoverTest {

    @Mock
    private AdService adService;

    @InjectMocks
    private ScheduleExpiredAdRemover scheduleExpiredAdRemover;

    @Test
    void testRemoveExpiredAds() {
        scheduleExpiredAdRemover.removeExpiredAds();
        Mockito.verify(adService).removeExpiredAds(Mockito.anyInt());
    }

}