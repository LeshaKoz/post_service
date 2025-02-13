package faang.school.postservice.job;

import faang.school.postservice.service.AdService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScheduledExpiredAdRemover {
    private final AdService adService;

    @Scheduled(cron = "${app.schedule.task-cron}")
    public void startRemovingAds() {
        adService.startRemovingAds();
    }
}
