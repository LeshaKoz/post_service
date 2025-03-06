package faang.school.postservice.service.ad;

import faang.school.postservice.schedule.ScheduledExpiredAdRemover;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ScheduledExpiredAdRemoverTest {

    @Mock
    private AdService adService;

    @InjectMocks
    private ScheduledExpiredAdRemover scheduledExpiredAdRemover;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(scheduledExpiredAdRemover, "batchSize", 2);
    }

    @Test
    void testRemoveExpiredAds_Success() {
        List<Long> expiredAdsIds = List.of(1L, 2L, 3L, 4L, 5L);
        when(adService.findExpiredAds()).thenReturn(expiredAdsIds);

        scheduledExpiredAdRemover.removeExpiredAds();

        verify(adService, times(1)).findExpiredAds();

        verify(adService, times(1)).deleteAds(List.of(1L, 2L));
        verify(adService, times(1)).deleteAds(List.of(3L, 4L));
        verify(adService, times(1)).deleteAds(List.of(5L));
    }

    @Test
    void testRemoveExpiredAds_EmptyList() {
        when(adService.findExpiredAds()).thenReturn(List.of());

        scheduledExpiredAdRemover.removeExpiredAds();

        verify(adService, times(1)).findExpiredAds();
        verify(adService, never()).deleteAds(anyList());
    }

    @Test
    void testRemoveExpiredAds_InvalidBatchSize() {
        ReflectionTestUtils.setField(scheduledExpiredAdRemover, "batchSize", 0);

        assertThrows(IllegalArgumentException.class, () -> scheduledExpiredAdRemover.removeExpiredAds());
    }
}