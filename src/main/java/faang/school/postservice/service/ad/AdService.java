package faang.school.postservice.service.ad;

import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.repository.ad.AdRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Slf4j
public class AdService implements DisposableBean {

    private final AdRepository adRepository;
    private final ExecutorService executorService;

    @Value("${expired.ad.remover.batch_size}")
    private int batchSize;

    public AdService(AdRepository adRepository) {
        this.adRepository = adRepository;
        this.executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    @Transactional
    public void deleteExpiredAds() {
        List<Ad> expiredAds = adRepository.findExpiredAds();

        if (expiredAds.isEmpty()) {
            log.info("There are no expired ads to delete");
            return;
        }

        List<Long> expiredAdIds = expiredAds.stream()
                .map(Ad::getId)
                .toList();

        List<List<Long>> partitions = partitionList(expiredAdIds, batchSize);

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (List<Long> partition : partitions) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                adRepository.deleteAllById(partition);
            }, executorService);
            futures.add(future);
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        log.info("The removal of expired ads has been completed");
    }

    private List<List<Long>> partitionList(List<Long> expiredAds, int batchSize) {
        List<List<Long>> partitions = new ArrayList<>();
        for (int i = 0; i < expiredAds.size(); i += batchSize) {
            partitions.add(expiredAds.subList(i, Math.min(i + batchSize, expiredAds.size())));
        }
        return partitions;
    }

    @Override
    public void destroy() throws Exception {
        executorService.shutdown();
    }
}
