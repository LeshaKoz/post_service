package faang.school.postservice.schedule;

import faang.school.postservice.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentModerator {

    private final CommentService commentService;

    @Scheduled(cron = "${comment.schedule.verify}")
    public void moderateComments() {
        commentService.verifyComments();
    }
}
