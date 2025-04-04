package faang.school.postservice.scheduler;

import faang.school.postservice.service.PostService;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

@RequiredArgsConstructor
@Component
public class ModerationScheduler {
    private final PostService postService;
    private final ExecutorService moderationExecutor;


    @Scheduled(cron = "${moderation.job.cron}") // "0 0 2 * * ?"
    public void runModerationJob() {
        postService.moderatePosts();
    }

    @PreDestroy
    public void shutdown() {
        moderationExecutor.shutdown();
    }
}
