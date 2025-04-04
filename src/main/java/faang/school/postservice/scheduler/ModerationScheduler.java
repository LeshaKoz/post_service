package faang.school.postservice.scheduler;

import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Компонент для периодической модерации постов по расписанию.
 * <p>
 * Использует Spring Scheduling для автоматического вызова модерации неверифицированных постов
 * согласно заданному cron-выражению. Cron-выражение настраивается через свойство
 * {@code moderation.cron.expression} в конфигурации приложения.
 * </p>
 */
@Component
@RequiredArgsConstructor
public class ModerationScheduler {
    private final PostService postService;

    /**
     * Метод для периодической модерации неверифицированных постов.
     * <p>
     * Запускается автоматически по расписанию, определенному в свойстве {@code moderation.cron.expression}.
     * Логика модерации делегируется сервису {@link PostService}.
     * </p>
     *
     * @see PostService#moderateUnverifiedPost()
     */
    @Scheduled(cron = "${moderation.cron.expression}")
    public void moderatePost(){
        postService.moderateUnverifiedPost();
    }
}