package faang.school.postservice.correcer;

import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostCorrecter {
    private static final String CORRECTING_POSTS_FORM = "{} correcting the spelling of the posts";

    private final PostService postService;

    @Async("postSpellingCorrecter")
    @Scheduled(cron = "${cron.correct_post}")
    private void correctingSpellingOfPosts() {
        log.info(CORRECTING_POSTS_FORM, "Start");
        postService.getAllPosts().forEach(postService::correctingSpellingPost);
        log.info(CORRECTING_POSTS_FORM, "Finish");
    }
}
