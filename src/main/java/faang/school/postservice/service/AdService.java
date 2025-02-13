package faang.school.postservice.service;

import faang.school.postservice.model.ad.Ad;
import faang.school.postservice.repository.ad.AdRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdService {
    private final AdRepository adRepository;
    private final int countThreadPool = 5;

    public void startRemovingAds() {
        List<Long> listAdId = new CopyOnWriteArrayList<>();
        List<Ad> ads = adRepository.findAll();
        ExecutorService executorService = Executors.newFixedThreadPool(countThreadPool);
        List<Future<?>> futures = new ArrayList<>();
        for (Ad ad : ads) {
            Future<?> future = executorService.submit(() -> {
                if (ad.getAppearancesLeft() == 0 ||
                        (ad.getEndDate() != null && ad.getEndDate().isBefore(LocalDateTime.now()))) {
                    listAdId.add(ad.getId());
                }
            });
            futures.add(future);
        }

        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (InterruptedException ex) {
                log.error("Прерывание потока", ex);
                Thread.currentThread().interrupt();
            } catch (ExecutionException ex) {
                log.error("Ошибка удаления просроченных реклам", ex);
            }
        }
        if (!listAdId.isEmpty()) {
            adRepository.deleteAllById(listAdId);
            log.debug("Удалено {} просроченных объявлений", listAdId.size());
        } else {
            log.debug("Нет просроченных объявлений для удаления");
        }

        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                log.warn("Не все задачи были завершены за отведённое время. Принудительная остановка пула потоков.");
                executorService.shutdownNow();
            }
        } catch (InterruptedException ex) {
            log.error("Прервано ожидание завершения пула потоков", ex);
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
