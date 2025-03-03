package faang.school.postservice.schedulers;

import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.repository.ad.AdRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AsyncAdRemovalServiceTest {

    @Mock
    private AdRepository adRepository;

    @InjectMocks
    private AsyncAdRemovalService asyncAdRemovalService;

    private List<Ad> batch;
    private final int BATCH_SIZE = 1000;

    @BeforeEach
    public void setUp() {
        batch = List.of(new Ad(), new Ad(), new Ad());
    }

    @Test
    public void testProcessBatch() {
        asyncAdRemovalService.processBatch(batch, BATCH_SIZE);

        verify(adRepository).deleteAll(batch);
    }
}
