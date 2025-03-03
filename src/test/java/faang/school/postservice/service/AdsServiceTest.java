package faang.school.postservice.service;

import faang.school.postservice.schedulers.AdsExpirationManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AdsServiceTest {

    @InjectMocks
    private AdsService adsService;

    @Mock
    private AdsExpirationManager adsExpirationManager;

    @Test
    public void testDeleteExpiredPremiums() {
        adsService.deleteExpiredAds();
        verify(adsExpirationManager).deleteExpiredAds();
    }
}
