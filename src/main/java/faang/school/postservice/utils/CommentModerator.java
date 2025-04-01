package faang.school.postservice.utils;


import faang.school.postservice.service.comment.CommentServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommentModerator {
    private final CommentServiceImpl commentServiceImpl;

    @Scheduled(cron = "${comments.moderation.cron}")
    public void moderateComments() {
        commentServiceImpl.moderateComments();
    }
}