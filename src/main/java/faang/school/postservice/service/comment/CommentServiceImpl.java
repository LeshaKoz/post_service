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

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private static final double TOXICITY_THRESHOLD = 0.35;

    private final CommentRepository commentRepository;
    private final CommentAnalyzer commentAnalyzer;

    @Value("${moderation.comments.batch-size}")
    private int batchSize;

    @Value("${moderation.comments.max-attempts}")
    private int maxAttempts;

    @Value("${moderation.comments.backoff-delay}")
    private int backoffDelay;

    public Mono<Void> moderateComments() {
        log.info("Comment moderation started");
        return Mono.fromCallable(commentRepository::count)
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(count -> {
                    int batches = (int) (count + batchSize - 1) / batchSize;
                    return Flux.range(0, batches);
                })
                .flatMap(batchNumber -> {
                    Pageable pageable = PageRequest.of(batchNumber, batchSize);
                    return Mono.fromCallable(() -> commentRepository.findComments(pageable))
                            .subscribeOn(Schedulers.boundedElastic())
                            .flatMapMany(page -> Flux.fromIterable(page.getContent()));
                })
                .flatMap(this::moderateComment)
                .then()
                .doOnSuccess(v -> log.info("Comment moderation completed"))
                .doOnError(e -> log.error("Error while moderating comments"));
    }

    private Mono<Void> moderateComment(Comment comment) {
        return commentAnalyzer.analyzeComment(comment.getContent())
                .retryWhen(Retry.backoff(maxAttempts, Duration.ofSeconds(backoffDelay))
                        .filter(ex -> ex instanceof CommentAnalyzerException))
                .flatMap(toxicityScore -> {
                    boolean moderationFailed = toxicityScore.getAttributeScores().values().stream()
                            .anyMatch(attributeScore -> attributeScore.getSummaryScore().getValue()
                                    >= TOXICITY_THRESHOLD || attributeScore.getSpanScores().stream().anyMatch(
                                    spanScore -> spanScore.getScore().getValue() >= TOXICITY_THRESHOLD));

                    log.info("Comment with ID {} and content '{}' {} moderation",
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
