package faang.school.postservice.schedule;

import faang.school.postservice.service.ad.AdService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class ScheduledExpiredAdRemover {

    private final AdService adService;

    @Value("${ad-service.ad-removal.batch-size}")
    private int batchSize;

    @PostConstruct
    private void checkBatchSize(){
        if (batchSize <= 0) {
            throw new IllegalArgumentException("Batch size must be greater than 0");
        }
    }

    @Scheduled(cron = "${ad-service.ad-removal.cron}")
    public void removeExpiredAds() {
        List<Long> expiredAdsIds = adService.findExpiredAds();
        List<List<Long>> batches = ListUtils.partition(expiredAdsIds, batchSize);

        CompletableFuture<?>[] futures = batches.stream()
                .map(batch -> CompletableFuture.runAsync(() -> adService.deleteAds(batch)))
                .toArray(CompletableFuture[]::new);

        CompletableFuture.allOf(futures).join();
    }
}