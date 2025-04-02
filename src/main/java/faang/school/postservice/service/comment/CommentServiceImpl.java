package faang.school.postservice.service.comment;

import faang.school.postservice.client.CommentAnalyzer;
import faang.school.postservice.exception.CommentAnalyzerException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.IntStream;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private static final double TOXICITY_THRESHOLD = 0.35;

    private final CommentRepository commentRepository;
    private final CommentAnalyzer commentAnalyzer;

    @Value("${moderation.comments.batch-size}")
    private int commentModerationBatchSize;

    @Value("${moderation.comments.max-attempts}")
    private int commentModerationMaxAttempts;

    @Value("${moderation.comments.backoff-delay}")
    private int commentModerationBackoffDelay;

    @Value("${moderation.comments.timeout-hours}")
    private int commentModerationTimeoutHours;

    @Value("${moderation.ban-users-for-comments.batch-size}")
    private int banBatchSize;

    @Value("${moderation.ban-users-for-comments.max-attempts}")
    private int userBanMaxAttempts;

    @Value("${moderation.ban-users-for-comments.backoff-delay}")
    private int userBanBackoffDelay;

    @Value("${moderation.ban-users-for-comments.thread-pool-size}")
    private int userBanThreadPoolSize;

    @Value("${moderation.ban-users-for-comments.timeout-hours}")
    private int userBanTimeoutHours;

    @Value("${moderation.ban-users-for-comments.ban-threshold}")
    private int userBanThreshold;

    public Mono<Void> moderateComments() {
        log.info("Comment moderation started");
        return Mono.fromCallable(commentRepository::count)
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(count -> {
                    int batches = (int) (count + commentModerationBatchSize - 1) / commentModerationBatchSize;
                    return Flux.range(0, batches);
                })
                .flatMap(batchNumber -> {
                    Pageable pageable = PageRequest.of(batchNumber, commentModerationBatchSize);
                    return Mono.fromCallable(() -> commentRepository.findComments(pageable))
                            .subscribeOn(Schedulers.boundedElastic())
                            .flatMapMany(page -> Flux.fromIterable(page.getContent()));
                })
                .flatMap(this::moderateComment)
                .timeout(Duration.ofHours(commentModerationTimeoutHours))
                .then()
                .doOnSuccess(v -> log.info("Comment moderation completed"))
                .doOnError(e -> log.error("Error while moderating comments"));
    }

    public void banUsersForComments() {
        log.info("User ban process started");
        ExecutorService executor = Executors.newFixedThreadPool(userBanThreadPoolSize);
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        int commentCount = (int) commentRepository.count();
        int batches = (commentCount + banBatchSize - 1) / banBatchSize;

        IntStream.range(0, batches).forEach(batchNumber -> {
            Pageable pageable = PageRequest.of(batchNumber, banBatchSize);
            futures.add(CompletableFuture.runAsync(() -> {
                banUserForComments(commentRepository.findUnverifiedComments(pageable).getContent());
            }, executor));
        });

        try {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get(userBanTimeoutHours, TimeUnit.HOURS);
            log.info("User ban process completed");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread interrupted while waiting for user ban execution", e);
            executor.shutdownNow();
            log.warn("User ban process was interrupted and may not have completed");
        } catch (ExecutionException e) {
            log.error("Execution exception while submitting posts for review. ", e);
        } catch (TimeoutException e) {
            log.warn("User ban process did not complete within timeout: {} hours", userBanTimeoutHours, e);
        } finally {
            executor.shutdown();
        }
    }

    private void banUserForComments(List<Comment> comments) {
        Map<Long, Integer> unverifiedComments = new HashMap<>();
        for (Comment comment : comments) {
            if (!comment.isVerified()) {
                unverifiedComments.putIfAbsent(comment.getId(), 0);
                unverifiedComments.put(comment.getId(), unverifiedComments.get(comment.getId()) + 1);
            }
        }
        for (var entry : unverifiedComments.entrySet()) {
            if (entry.getValue() >= userBanThreshold) {
                //
            }
        }
    }

    private Mono<Void> moderateComment(Comment comment) {
        return commentAnalyzer.analyzeComment(comment.getContent())
                .retryWhen(Retry.backoff(commentModerationMaxAttempts, Duration.ofSeconds(commentModerationBackoffDelay))
                        .filter(ex -> ex instanceof CommentAnalyzerException))
                .flatMap(toxicityScore -> {
                    boolean moderationFailed = toxicityScore.getAttributeScores().values().stream()
                            .anyMatch(attributeScore -> attributeScore.getSummaryScore().getValue()
                                    >= TOXICITY_THRESHOLD || attributeScore.getSpanScores().stream().anyMatch(
                                    spanScore -> spanScore.getScore().getValue() >= TOXICITY_THRESHOLD));

                    log.debug("Comment with ID {} and content '{}' {} moderation",
                            comment.getId(), comment.getContent(), moderationFailed ? "failed" : "passed");
                    comment.setVerified(!moderationFailed);
                    comment.setVerifiedDate(LocalDateTime.now());

                    return Mono.fromCallable(() -> commentRepository.save(comment))
                            .subscribeOn(Schedulers.boundedElastic())
                            .then();
                })
                .onErrorResume(e -> {
                    log.error("Could not moderate comment with ID {} and content {}",
                            comment.getId(), comment.getContent());
                    return Mono.empty();
                });
    }
}
