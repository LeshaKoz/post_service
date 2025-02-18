package faang.school.postservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduledPostPublisher {
    private final PostService postService;

    @Scheduled(cron = "${publish-scheduler.config.cronExpression}")
    public void postPublisherSchedule() {
        log.info("Publish scheduler started processing");
        postService.publishScheduledPosts();
        log.info("Publish scheduler finished processing");
    }
}
