package faang.school.postservice.scheduler;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.exception.IntegrationException;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.support.RetrySynchronizationManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class Scheduler {
    private final UserContext userContext;
    private final PostService postService;

    @Transactional
    @Scheduled(cron = "${spell-service.cron}")
    @Async("spellServicePool")
    @Retryable(retryFor = IntegrationException.class,
            maxAttemptsExpression = "${spell-service.retry.attempts}",
            backoff = @Backoff(delayExpression = "${spell-service.retry.delay}",
                    multiplierExpression = "${spell-service.retry.multiplier}"))
    public void correctAllUnpublishedPosts() {
        userContext.setUserId(1);
        if (Optional.ofNullable(userContext.getUserId()).isPresent()) {
            log.info("мы тут");
            postService.correctAllUnpublishedPosts();
        }
    }

    @Recover
    void recover(IntegrationException e) {
        var retryCount = RetrySynchronizationManager.getContext().getRetryCount();
        log.info("Попытки повторного вызова сервиса завершились неудачей. Кол-во попыток = {}", retryCount);
    }
}
