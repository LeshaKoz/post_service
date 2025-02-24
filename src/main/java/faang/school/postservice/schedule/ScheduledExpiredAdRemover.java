package faang.school.postservice.schedule;

import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.service.ad.AdService;
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

    @Scheduled(cron = "${ad-service.ad-removal.cron}")
    public void removeExpiredAds() {
        if (batchSize <= 0) {
            throw new IllegalArgumentException("batchSize must be greater than 0");
        }
        List<Ad> expiredAds = adService.findExpiredAds();
        List<List<Ad>> batches = ListUtils.partition(expiredAds, batchSize);

        CompletableFuture<?>[] futures = batches.stream()
                .map(batch -> CompletableFuture.runAsync(() -> adService.deleteAds(batch)))
                .toArray(CompletableFuture[]::new);

        CompletableFuture.allOf(futures).join();
    }
}