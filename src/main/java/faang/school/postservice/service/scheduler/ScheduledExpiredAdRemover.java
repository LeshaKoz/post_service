package faang.school.postservice.service.scheduler;

import faang.school.postservice.service.ad.AdService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledExpiredAdRemover {

    private final AdService adService;

    @Scheduled(cron = "${expired.ad.remover.cron}")
    public void removeExpiredAds() {
        log.info("Starting the removal of expired ads on a schedule");
        adService.deleteExpiredAds();
        log.info("Expired ad removal completed");
    }
}