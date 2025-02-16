package faang.school.postservice.scheduler.post;

import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthorBannerScheduler {
    private final PostService postService;

    @Scheduled(cron = "${author.ban.cron}")
    @SchedulerLock(name = "banUsersWithUnverifiedPosts")
    public void banUsersWithUnverifiedPosts() {
        log.info("Send request to ban users");
        postService.banUsersWithManyUnverifiedPosts();
    }
}
