package faang.school.postservice.correcter;

import faang.school.postservice.service.PostService;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class PostCorrecter {

    private final PostService postService;

    @Scheduled(cron = "${schedule.correct-posts}")
    public void correctPostJob() {
        postService.correctPosts();
    }
}
