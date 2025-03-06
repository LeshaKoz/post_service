package faang.school.postservice.schedule;


import faang.school.postservice.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommenterBanner {

    private final CommentService commentService;

    @Scheduled(cron = "${comment.schedule.user-ban}")
    public void banCommenters() {
        commentService.publishUsersToBanEvent();
    }
}
