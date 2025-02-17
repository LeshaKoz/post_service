package faang.school.postservice.service.schedulers;

import faang.school.postservice.service.post.PostService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostScheduler {

    private final PostService postService;

    @Scheduled(cron = "${post.grammar.cronPeriod}")
    public void checkGrammar() {
        postService.checkGrammar();
    }

    @Scheduled(cron = "${post.moderation.cronPeriod}")
    public void scheduledVerifyPosts() {
        postService.moderatePosts();
    }

    @Scheduled(cron = "${post.schedule.scheduled-cron}")
    public void publishScheduledPosts() {
        postService.publishScheduledPosts();
    }
}
