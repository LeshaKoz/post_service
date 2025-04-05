package faang.school.postservice.service.post_correct;

import faang.school.postservice.service.post.interfaces.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostCorrecter {
    private final PostService postService;

    @Scheduled(cron = "${scheduler.post-correction.cron}")
    public void correctPostsDaily() {
        postService.correctUnpublishedPosts();
    }
}
