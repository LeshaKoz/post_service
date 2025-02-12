package faang.school.postservice.scheduler;

import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthorBanner {
    private final PostService postService;

    @Scheduled(cron = "${author.banner.scheduler.cron}")
    public void startAuthorBanner() {
        log.info("Author Banner started");
        postService.postAuthorsToBan();
        log.info("Author Banner ends");
    }
}
