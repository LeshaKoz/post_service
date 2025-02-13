package faang.school.postservice.scheduler;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.exception.IntegrationException;
import faang.school.postservice.service.PostService;
import faang.school.postservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.support.RetrySynchronizationManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class Scheduler {
    private final UserService userService;
    private final PostService postService;

    @Transactional
    @Scheduled(cron = "${spell-service.cron}")
    @Async("threadPool")
    @Retryable(retryFor = IntegrationException.class,
            maxAttemptsExpression = "${spell-service.retry.attempts}",
            backoff = @Backoff(delayExpression = "${spell-service.retry.delay}",
                    multiplierExpression = "${spell-service.retry.multiplier}"))
    public void correctAllUnpublishedPosts() {
        if (userService.isUserExistsInContext()) {
            log.info("Начало запланированного события");
            postService.correctAllUnpublishedPosts();
            log.info("Конец запланированного события");
        }
    }

    @Recover
    void recover(IntegrationException e) {
        var retryCount = RetrySynchronizationManager.getContext().getRetryCount();
        log.warn("Попытки повторного вызова сервиса завершились неудачей. Кол-во попыток = {}", retryCount);
    }
}
