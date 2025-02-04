package faang.school.postservice.scheduler.post;

import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ScheduledPostPublisher {
    private final PostService postService;

    @Scheduled(cron = "${post-service.publish.scheduled.cron}")
    public void startPublishPosts() {
        postService.publishScheduledPosts();
    }
}
