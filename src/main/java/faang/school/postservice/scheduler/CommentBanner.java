package faang.school.postservice.scheduler;

import faang.school.postservice.service.CommentServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommentBanner implements Banner {

    private final CommentServiceImpl commentService;

    @Scheduled(cron = "${task.scheduling.comment-ban.cron_expression}")
    @Override
    public void startBanUsers() {
        log.debug("Starting moderate comments");
        commentService.collectAndPushUsersForBan();
        log.debug("All comments moderated");
    }
}
