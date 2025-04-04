package faang.school.postservice.service;

import faang.school.postservice.repository.ad.AdRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdService {

    private final AdRepository adRepository;

    @Value("${thread-pool.delete-expired-posts-max-threads}")
    private int deleteExpiredPostsThreads;

    public void findExpiredPostByDateEnd() {
        List<Long> listIds = adRepository.findExpiredPostByDateEnd(LocalDateTime.now());

        if (listIds.isEmpty()) {
            return;
        }

        int portion = listIds.size() / deleteExpiredPostsThreads;
        if (portion > 0) {
            deleteAdsByPortions(listIds, portion);
        } else {
            deleteAdsByIds(listIds);
        }
    }

    private void deleteAdsByPortions(List<Long> listIds, int portion) {
        ExecutorService executorService = Executors.newFixedThreadPool(deleteExpiredPostsThreads);

        for (int i = 0; i < deleteExpiredPostsThreads; i++) {
            int startIndex = i * portion;
            int endIndex = (i == deleteExpiredPostsThreads - 1) ? listIds.size() : startIndex + portion;

            var subList = new ArrayList<>(listIds.subList(startIndex, endIndex));
            deleteAdsByIds(subList);
        }

        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            log.error("Thread was interrupted while deleting ads", e);
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private void deleteAdsByIds(List<Long> listIds) {
        for (long id : listIds) {
            adRepository.deleteById(id);
        }
    }
}
