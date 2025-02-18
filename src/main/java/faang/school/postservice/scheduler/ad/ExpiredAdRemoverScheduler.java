package faang.school.postservice.scheduler.ad;

import faang.school.postservice.service.ad.AdService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExpiredAdRemoverScheduler {

    private final AdService adService;

    @Scheduled(cron = "${ad.expired.cron}")
    @SchedulerLock(name = "deleteExpiredAds")
    public void deleteExpiredAds() {
        log.info("Deleting expired ads...");
        adService.deleteExpiredAds();
        log.info("Expired ads deleted");
    }
}
