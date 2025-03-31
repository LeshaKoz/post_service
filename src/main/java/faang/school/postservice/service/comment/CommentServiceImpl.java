package faang.school.postservice.service.comment;

import faang.school.postservice.exception.CommentAnalyzerException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private static final int TIMEOUT_HOURS = 3;

    private final CommentRepository commentRepository;

    @Value("${comments.moderation.batch-size}")
    private int batchSize;

    @Value("${comments.moderation.thread-pool-size}")
    private int threadPoolSize;

    public void moderateComments() {
        ExecutorService executor = Executors.newFixedThreadPool(threadPoolSize);
        long commentsCount = commentRepository.count();
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        AtomicBoolean hasErrors = new AtomicBoolean(false);

        for (int start = 0; start < commentsCount; start += batchSize) {
            int end = Math.min(start + batchSize - 1, (int) commentsCount);
            int size = end - start + 1;
            int finalStart = start;
            futures.add(CompletableFuture.runAsync(() -> {
                Pageable pageable = PageRequest.of((finalStart + size - 1) / size, size);
                Page<Comment> comments = commentRepository.findComments(pageable);
                moderateCommentsBatch(comments.getContent());
            }, executor));
        }
        try {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get(TIMEOUT_HOURS, TimeUnit.HOURS);
            if (!hasErrors.get()) {
                log.info("Comment moderation completed successfully");
            } else {
                log.info("Not all comments passed moderation");
            }
        } catch (ExecutionException e) {
            log.error("ExecutionException while moderating comments", e);
        } catch (InterruptedException e) {
            log.error("Comment moderation was interrupted", e);
            Thread.currentThread().interrupt();
        } catch (TimeoutException e) {
            log.error("Comment moderation haven't completed on time");
        } finally {
            executor.shutdown();
        }
    }

    @Retryable(
            retryFor = {CommentAnalyzerException.class},
            backoff = @Backoff(delayExpression = "${comments.moderation.backoff-delay}")
    )
    private void moderateCommentsBatch(List<Comment> comments) {

    }

    @Recover
    private void recoverModerateCommentsBatch(CommentAnalyzerException e) {

    }
}
