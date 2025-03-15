package faang.school.postservice.scheduler.moderate;

import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ModerationScheduler {
    private final PostService postService;

    @Scheduled(cron = "${moderation.cron}")
    public void moderatePosts() {
        log.info("Начало модерации постов...");
        postService.moderateUnverifiedPosts();
        log.info("Конец модерации постов");
    }
}
