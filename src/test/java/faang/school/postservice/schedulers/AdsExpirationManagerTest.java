package faang.school.postservice.schedulers;

import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.repository.ad.AdRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdsExpirationManagerTest {

    @Mock
    private AdRepository adRepository;

    @Mock
    private AsyncAdRemovalService asyncAdRemovalService;

    @InjectMocks
    private AdsExpirationManager adsExpirationManager;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(adsExpirationManager, "batchSize", 1000);
    }

    @Test
    public void testDeleteExpiredPremiums() {
        Page<Ad> page = mock(Page.class);
        when(page.getContent()).thenReturn(Collections.emptyList());
        when(page.hasNext()).thenReturn(false);
        when(adRepository.findExpiredOrDepletedAds(any(), any())).thenReturn(page);

        adsExpirationManager.deleteExpiredAds();

        verify(adRepository).findExpiredOrDepletedAds(any(), any());
        verify(asyncAdRemovalService).processBatch(anyList(), anyInt());
    }
}
