package faang.school.postservice.service.scheduler;

import faang.school.postservice.service.PostService;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class PostCorrecter {

    private final PostService postService;

    @Scheduled(cron = "0 0 0 * * *")
    public void postCorrections() {
        postService.postCorrections();
    }
}
