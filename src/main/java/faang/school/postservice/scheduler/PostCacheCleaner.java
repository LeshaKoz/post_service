package faang.school.postservice.scheduler;

import faang.school.postservice.service.PostCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PostCacheCleaner {
    private final PostCacheService postCacheService;

    @Scheduled(cron = "${schedulers.post-cache.cleanup-cron}")
    public void cleanupExpiredCache() {
        postCacheService.cleanupExpiredPosts();
    }
}
