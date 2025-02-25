package faang.school.postservice.service.comment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class CommentModerator {
    private final CommentService commentService;

    @Scheduled(cron = "${cron.comments.check-delay}")
    void checkComments() {
        log.info("Start checking comments by cron expression");
        try {
            commentService.checkComments();
        } catch (TaskRejectedException e) {
            log.error("Task is already running");
        }
    }
}
