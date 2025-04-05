package faang.school.postservice.scheduler;


import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
@RequiredArgsConstructor
public class ScheduledPostPublisher {

    private final PostService postService;

    @Scheduled(cron = "0 * * * * *")
    public void publishScheduledPosts() {
        postService.publishScheduledPosts();
    }


}
