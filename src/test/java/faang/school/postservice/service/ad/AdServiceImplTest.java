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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdServiceImplTest {

    @Mock
    private AdRepository adRepository;

    @InjectMocks
    private AdServiceImpl adService;

    private Ad expiredAd;
    private Ad activeAd;

    @BeforeEach
    void setUp() {
        expiredAd = Ad.builder()
                .id(1L)
                .endDate(LocalDateTime.now().minusDays(1))  // Просрочен
                .appearancesLeft(0)
                .build();

        activeAd = Ad.builder()
                .id(2L)
                .endDate(LocalDateTime.now().plusDays(1))  // Активен
                .appearancesLeft(10)
                .build();
    }

    @Test
    void testFindExpiredAds() {
        when(adRepository.findAll()).thenReturn(List.of(expiredAd, activeAd));

        List<Ad> result = adService.findExpiredAds();

        assertEquals(1, result.size());
        assertEquals(expiredAd.getId(), result.get(0).getId());
        verify(adRepository, times(1)).findAll();
    }

    @Test
    void testDeleteAds() {
        List<Ad> adsToDelete = List.of(expiredAd);

        adService.deleteAds(adsToDelete);
        verify(adRepository, times(1)).deleteAllById(List.of(expiredAd.getId()));
    }
}