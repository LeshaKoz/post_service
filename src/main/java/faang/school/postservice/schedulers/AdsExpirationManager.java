package faang.school.postservice.schedulers;

import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.repository.ad.AdRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Component
public class AdsExpirationManager {
    private final AdRepository adRepository;
    private final AsyncAdRemovalService asyncAdRemovalService;

    @Value("${ad.batch.size}")
    private int batchSize;

    @Transactional
    public void deleteExpiredAds() {
        int pageNumber = 0;
        Page<Ad> page;

        do {
            page = adRepository.findExpiredOrDepletedAds(LocalDateTime.now(),
                    PageRequest.of(pageNumber, batchSize));
            List<Ad> expiredAds = page.getContent();
            asyncAdRemovalService.processBatch(expiredAds, batchSize);
            pageNumber++;
        } while (page.hasNext());
    }
}
