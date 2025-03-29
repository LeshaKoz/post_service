package faang.school.postservice.service;

import faang.school.postservice.client.LanguageToolClient;
import faang.school.postservice.exception.LanguageToolException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
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
public class PostCorrectionService {
    @Value("${posts.correction.batch-size}")
    int batchSize;

    @Value("${posts.correction.thread-poop-size}")
    int threadPoolSize;

    private static final int TIMEOUT_HOURS = 2;

    private final PostRepository postRepository;
    private final PostService postService;
    private final LanguageToolClient languageToolClient;

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
                sendPostContentsChecking(postContents.getContent());
            }, executor));
        }

        try {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get(TIMEOUT_HOURS, TimeUnit.HOURS);
            log.info("Submitting posts for review completed successfully");
        } catch (TimeoutException e) {
            log.error("Submitting posts for review haven't completed on time");
        } catch (InterruptedException e) {
            log.error("Submitting posts for review was interrupted. {}", e.getMessage());
        } catch (ExecutionException e) {
            log.error("Exception during Submitting posts for review. {}", e.getMessage());
        }
    }

    @Retryable(
            retryFor = {LanguageToolException.class},
            maxAttemptsExpression = "${spring.retry.language-tool.max-attempts}",
            backoff = @Backoff(delayExpression = "${spring.retry.language-tool.backoff-delay}")
    )
    public void sendPostContentsChecking(List<Post> posts) {
        for (Post post : posts) {
            log.info("Before correcting errors in the text: {}", post.getContent());
            post.setContent(languageToolClient.getCorrectedText(
                    post.getContent(), "auto").block());
            postRepository.save(post);
            log.info("After correcting errors in the text: {}", post.getContent());
        }
    }

    @Recover
    public void recoverSendPostContentsChecking(LanguageToolException e) {
        log.error("Failed to correct text after retries. {}", e.getMessage());
    }
}
