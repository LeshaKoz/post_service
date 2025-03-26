package faang.school.postservice.service.post;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostCacheRepository;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class AsyncPostPublishPerformer {
    private final PostRepository postRepository;
    private final PostCacheRepository postCacheRepository;

    @Async("publishExecutor")
    public void publishBatch(List<Post> posts) {
        posts.forEach(post -> {
            post.setPublished(true);
            post.setPublishedAt(LocalDateTime.now());
        });
        postRepository.saveAll(posts);
        postCacheRepository.saveAll(posts);
        log.info("Scheduled task #Publish post# completed");
    }
}