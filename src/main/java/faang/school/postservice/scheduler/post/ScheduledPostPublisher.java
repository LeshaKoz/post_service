package faang.school.postservice.scheduler.post;

import faang.school.postservice.repository.PostCacheRepository;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ScheduledPostPublisher {
    private final PostService postService;
    private final PostCacheRepository postCacheRepository;

    @Scheduled(cron = "${post-service.publish.scheduled.cron}")
    public void startPublishPosts() {
        postService.publishScheduledPosts();
    }

    @Scheduled(cron = "${spring.data.redis.clear-cache.cron}")
    public void clearCache() {
        postCacheRepository.clearCache();
    }
}