package faang.school.postservice.service.ad;

import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.schedule.ScheduledExpiredAdRemover;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScheduledExpiredAdRemoverTest {

    @Mock
    private AdService adService;

    @InjectMocks
    private ScheduledExpiredAdRemover scheduledExpiredAdRemover;

    private Ad expiredAd;

    @BeforeEach
    void setUp() {
        expiredAd = Ad.builder()
                .id(1L)
                .endDate(LocalDateTime.now().minusDays(1))
                .appearancesLeft(0)
                .build();
    }

    @Test
    void testRemoveExpiredAds() {
        ReflectionTestUtils.setField(scheduledExpiredAdRemover, "batchSize", 10);

        List<Ad> expiredAds = List.of(expiredAd);

        when(adService.findExpiredAds()).thenReturn(expiredAds);

        scheduledExpiredAdRemover.removeExpiredAds();

        verify(adService, times(1)).findExpiredAds();
        verify(adService, times(1)).deleteAds(expiredAds);
    }
}