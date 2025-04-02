package faang.school.postservice.utils;

import faang.school.postservice.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentBanner {
    private final CommentService commentService;

    @Scheduled(cron = "${moderation.ban-users-for-comments.cron}")
    public void commentBanner() {
        commentService.banUsersForComments();
    }
}
