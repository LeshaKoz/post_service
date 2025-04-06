package faang.school.postservice.listener;

import faang.school.postservice.event.kafka.PostViewKafkaEvent;
import faang.school.postservice.model.cache.PostRedis;
import faang.school.postservice.repository.redis.PostRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaPostViewConsumer {
    private final PostRedisRepository postRedisRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.data.kafka.topics.postView.max-retries:5}")
    private int maxRetries;

    @KafkaListener(
            topics = "${spring.data.kafka.topics.postView.name}",
            groupId = "${spring.data.kafka.consumer.group-id}"
    )
    public void consume(PostViewKafkaEvent event, Acknowledgment acknowledgment) {
        log.info("Получено событие просмотра поста: {}", event);
        String redisKey = getRedisKey(event);

        boolean updated = tryUpdateViews(event, redisKey);
        if (updated) {
            acknowledgment.acknowledge();
        } else {
            log.error("Не удалось обновить просмотры для публикации {} после {} попыток", event.getPostId(), maxRetries);
        }
    }

    private boolean tryUpdateViews(PostViewKafkaEvent event, String redisKey) {
        int attempt = 0;
        boolean success = false;

        while (!success && attempt < maxRetries) {
            attempt++;
            redisTemplate.watch(redisKey);
            try {
                Optional<PostRedis> foundPost = postRedisRepository.findById(String.valueOf(event.getPostId()));
                if (foundPost.isEmpty()) {
                    log.warn("Пост {} не найден в Redis", event.getPostId());
                    return false;
                }

                success = attemptTransaction(foundPost.get(), redisKey, event.getPostId(), attempt);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Прервано при повторной попытке транзакции Redis", e);
            }
        }

        return success;
    }

    private boolean attemptTransaction(PostRedis post, String redisKey, long postId, int attempt) throws InterruptedException {
        long newViewsCount = post.getViewsCount() + 1;

        redisTemplate.multi();
        post.setViewsCount(newViewsCount);
        postRedisRepository.save(post);

        List<Object> result = redisTemplate.exec();
        if (result != null && !result.isEmpty()) {
            log.info("Обновленные просмотры для публикации {}: {}", postId, newViewsCount);
            return true;
        }

        log.warn("Обнаружен конфликт, выполняется повторная попытка... Попытка № {}", attempt);
        TimeUnit.MILLISECONDS.sleep(10);
        return false;
    }

    private String getRedisKey(PostViewKafkaEvent event) {
        return "post:" + event.getPostId();
    }
}
