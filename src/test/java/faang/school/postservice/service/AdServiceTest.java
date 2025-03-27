package faang.school.postservice.service;

import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.repository.ad.AdRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AdServiceTest {

    @Mock
    private AdRepository adRepository;

    @Mock
    private AsyncDeleteService asyncDeleteService;

    @InjectMocks
    private AdService adService;

    private Ad createAd(LocalDateTime endDate, long appearancesLeft) {
        return Ad.builder()
                .endDate(endDate)
                .appearancesLeft(appearancesLeft)
                .build();
    }

    private LocalDateTime past;
    private LocalDateTime future;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();
        past = now.minusDays(1);
        future = now.plusDays(1);
    }

    @Test
    void testRemoveExpiredAds_noExpiredAds() {
        Ad ad1 = createAd(future, 5);
        Ad ad2 = createAd(future, 3);
        when(adRepository.findExpiredAds(any(LocalDateTime.class))).thenReturn(Collections.emptyList());

        adService.removeExpiredAds(2);

        verify(asyncDeleteService, never()).deleteExpiredBatch(anyList());
    }

    @Test
    void testRemoveExpiredAds_expiredByEndDate() {
        Ad ad1 = createAd(past, 5);
        Ad ad2 = createAd(past, 3);
        when(adRepository.findExpiredAds(any(LocalDateTime.class))).thenReturn(List.of(ad1, ad2));

        adService.removeExpiredAds(2);

        verify(asyncDeleteService).deleteExpiredBatch(argThat(list -> list.size() == 2));
    }

    @Test
    void testRemoveExpiredAds_expiredByAppearancesLeft() {
        Ad ad = createAd(future, 0);
        when(adRepository.findExpiredAds(any(LocalDateTime.class))).thenReturn(List.of(ad));

        adService.removeExpiredAds(1);

        verify(asyncDeleteService).deleteExpiredBatch(argThat(list -> list.size() == 1));
    }
}