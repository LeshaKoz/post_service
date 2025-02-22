package faang.school.postservice.service.schedulers;

import faang.school.postservice.service.post.PostProcessingService;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostScheduler {

    private final PostService postService;
    private final PostProcessingService postProcessingService;

    @Scheduled(cron = "${post.grammar.cronPeriod}")
    public void checkGrammar() {
        postProcessingService.checkGrammar();
    }

    @Scheduled(cron = "${post.moderation.cronPeriod}")
    public void scheduledVerifyPosts() {
        postProcessingService.moderatePosts();
    }

    @Scheduled(cron = "${post.schedule.scheduled-cron}")
    public void publishScheduledPosts() {
        postService.publishScheduledPosts();
    }
}
