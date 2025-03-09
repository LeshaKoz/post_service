package faang.school.postservice.service.comment;

import faang.school.postservice.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommenterBanner {

    private final CommentService commentService;

    @Scheduled(cron = "${moderation.cron-ban-user}")
    public void checkAndBanUsers() {
        log.info("Checking for users with too many unverified comments...");
        commentService.processUnverifiedComments();
    }
}
