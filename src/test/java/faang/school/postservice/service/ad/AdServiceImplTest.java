package faang.school.postservice.service.ad;

import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.repository.ad.AdRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdServiceImplTest {

    @Mock
    private AdRepository adRepository;

    @InjectMocks
    private AdServiceImpl adService;

    private Ad expiredAd1;
    private Ad expiredAd2;
    private Ad activeAd;

    @BeforeEach
    void setUp() {
        expiredAd1 = Ad.builder()
                .id(1L)
                .appearancesLeft(0)
                .endDate(LocalDateTime.now().minusDays(1))
                .build();

        expiredAd2 = Ad.builder()
                .id(2L)
                .appearancesLeft(5)
                .endDate(LocalDateTime.now().minusHours(1))
                .build();

        activeAd = Ad.builder()
                .id(3L)
                .appearancesLeft(10)
                .endDate(LocalDateTime.now().plusDays(1))
                .build();
    }

    @Test
    void testFindExpiredAds() {
        List<Ad> ads = Arrays.asList(expiredAd1, expiredAd2, activeAd);
        when(adRepository.findAll()).thenReturn(ads);

        List<Long> expiredAdsIds = adService.findExpiredAds();

        assertEquals(2, expiredAdsIds.size());
        assertEquals(1L, expiredAdsIds.get(0));
        assertEquals(2L, expiredAdsIds.get(1));
        verify(adRepository, times(1)).findAll();
    }

    @Test
    void testDeleteExpiredAds() {
        List<Ad> ads = Arrays.asList(expiredAd1, expiredAd2, activeAd);
        when(adRepository.findAll()).thenReturn(ads);

        List<Long> deletedAdsIds = adService.deleteExpiredAds();

        assertEquals(2, deletedAdsIds.size());
        assertEquals(1L, deletedAdsIds.get(0));
        assertEquals(2L, deletedAdsIds.get(1));
        verify(adRepository, times(1)).findAll();
        verify(adRepository, times(1)).deleteAllById(deletedAdsIds);
    }

    @Test
    void testDeleteAds() {
        List<Long> adsIds = Arrays.asList(1L, 2L);

        adService.deleteAds(adsIds);

        verify(adRepository, times(1)).deleteAllById(adsIds);
    }
}