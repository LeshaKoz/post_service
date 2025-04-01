package faang.school.postservice.service.comment;

import faang.school.postservice.client.CommentAnalyzer;
import faang.school.postservice.dto.commentAnalyzer.response.ToxicityScoreDto;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private static final int TIMEOUT_HOURS = 3;
    private static final double TOXICITY_THRESHOLD = 0.35;

    private final CommentRepository commentRepository;
    private final CommentAnalyzer commentAnalyzer;

    @Value("${moderation.comments.batch-size}")
    private int batchSize;

    @Value("${moderation.comments.thread-pool-size}")
    private int threadPoolSize;

    public void moderateComments() {
        log.info("Comment moderation started");
        ExecutorService executor = Executors.newFixedThreadPool(threadPoolSize);
        long commentsCount = commentRepository.count();
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (int start = 0; start < commentsCount; start += batchSize) {
            int end = Math.min(start + batchSize - 1, (int) commentsCount);
            int size = end - start + 1;
            int finalStart = start;
            futures.add(CompletableFuture.runAsync(() -> {
                Pageable pageable = PageRequest.of((finalStart + size - 1) / size, size);
                Page<Comment> comments = commentRepository.findComments(pageable);
                comments.forEach(this::moderateComment);
            }, executor));
        }

        try {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get(TIMEOUT_HOURS, TimeUnit.HOURS);
            log.info("Comment moderation completed");
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
    private void moderateComment(Comment comment) {
        ToxicityScoreDto toxicityScore = commentAnalyzer.analyzeComment(comment.getContent());
        boolean moderationFailed = toxicityScore.getAttributeScores().values().stream()
                .anyMatch(attributeScore -> attributeScore.getSummaryScore().getValue()
                        >= TOXICITY_THRESHOLD || attributeScore.getSpanScores().stream().anyMatch(
                        spanScore -> spanScore.getScore().getValue() >= TOXICITY_THRESHOLD));

        log.debug("Comment with ID {} and content '{}' {} moderation",
                comment.getId(), comment.getContent(), moderationFailed ? "failed" : "passed");

        comment.setVerified(!moderationFailed);
        comment.setVerifiedDate(LocalDateTime.now());
        commentRepository.save(comment);
    }

    @Recover
    private void recoverModerateComment(CommentAnalyzerException e, Comment comment) {
        log.error("Failed to moderate comment after retries", e);
        log.error("Comment with ID {} could not be moderated", comment.getId());
    }
}
