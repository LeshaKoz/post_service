package faang.school.postservice.schedulers;

import faang.school.postservice.service.AdsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledExpiredAdRemover {
    private final AdsService adsService;

    @Scheduled(cron = "${ad.schedule.removal-cron}")
    public void startRemovingAds() {
        adsService.deleteExpiredAds();
        log.debug("Все просроченные рекламы удалены!");
    }
}
