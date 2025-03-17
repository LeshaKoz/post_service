package faang.school.postservice.service;

import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.repository.ad.AdRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AsyncDeleteServiceTest {

    private static final Long AD_ID_FIRST = 1L;
    private static final Long AD_ID_SECOND = 2L;

    @Mock
    private AdRepository adRepository;

    @InjectMocks
    private AsyncDeleteService asyncDeleteService;

    @Test
    void testDeleteExpiredBatch() {
        List<Ad> ads = List.of(
                Ad.builder().id(AD_ID_FIRST).build(),
                Ad.builder().id(AD_ID_SECOND).build()
        );

        asyncDeleteService.deleteExpiredBatch(ads);

        verify(adRepository, times(1)).deleteAll(ads);
    }
}