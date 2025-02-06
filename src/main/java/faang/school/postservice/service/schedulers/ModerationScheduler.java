package faang.school.postservice.service.schedulers;

import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ModerationScheduler {

    private final PostService postService;

    @Scheduled(cron = "${post.cronPeriod}")
    public void scheduledVerifyPosts() {
        postService.moderatePosts();
    }
}
