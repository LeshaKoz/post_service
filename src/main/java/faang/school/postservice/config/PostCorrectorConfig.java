package faang.school.postservice.config;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
@Slf4j
@RequiredArgsConstructor
public class PostCorrectorConfig {
    @Value("${posts.correction.batch-size}")
    int batchSize;

    @Value("${posts.correction.thread-poop-size}")
    int threadPoolSize;

    private final PostRepository postRepository;
    private final PostService postService;
    private static final int TIMEOUT_HOURS = 2;

    @Scheduled(cron = "${posts.correction.cron}")
    public void sendPostsForChecking() {
        log.info("Submitting posts for review started");
        ExecutorService executor = Executors.newFixedThreadPool(threadPoolSize);
        long total = postRepository.count();
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (int start = 0; start < total; start += batchSize) {
            int end = Math.min(start + batchSize - 1, (int) total);
            int size = end - start + 1;
            int finalStart = start;
            futures.add(CompletableFuture.runAsync(() -> {
                Pageable pageable = PageRequest.of((finalStart + size - 1) / size, size);
                Page<Post> postContents = postRepository.findPosts(pageable);
                postService.sendPostContentsChecking(postContents.getContent());
            }, executor));
            break;
        }

        try {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get(TIMEOUT_HOURS, TimeUnit.HOURS);
            log.info("Submitting posts for review completed successfully");
        } catch (TimeoutException e) {
            log.error("Submitting posts for review haven't completed on time");
        } catch (InterruptedException e) {
            log.error("Submitting posts for review was interrupted", e);
        } catch (ExecutionException e) {
            log.error("Exception during Submitting posts for review", e);
        }
    }
}
