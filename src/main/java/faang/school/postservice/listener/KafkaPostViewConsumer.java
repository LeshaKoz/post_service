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

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaPostViewConsumer {
    private final PostRedisRepository postRedisRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.data.kafka.topic.post-views.max-retries:5}")
    private int maxRetries;

    @KafkaListener(topics = "${spring.data.kafka.topic.post-views.name}", groupId = "${spring.data.kafka.group-id}")
    public void consume(PostViewKafkaEvent event, Acknowledgment acknowledgment) {
        log.info("Получено событие просмотра поста: {}", event);
        String redisKey = "post:" + event.getPostId();

        boolean updated = false;
        int attempt = 0;

        while (!updated && attempt < maxRetries){
            attempt++;
            redisTemplate.watch(redisKey);
            try {
                Optional<PostRedis> foundPost = postRedisRepository.findById(String.valueOf(event.getPostId()));
                if(foundPost.isEmpty()){
                    log.warn("Пост {} не найден в Redis", event.getPostId());
                    return;
                }

                PostRedis post = foundPost.get();
                long newViewsCount = post.getViewsCount() + 1;

                redisTemplate.multi();
                post.setViewsCount(newViewsCount);
                postRedisRepository.save(post);

                if (!redisTemplate.exec().isEmpty()) {
                    updated = true;
                    log.info("Обновленные просмотры для публикации {}: {}", event.getPostId(), newViewsCount);
                    acknowledgment.acknowledge(); // Подтверждаем обработку сообщения
                } else {
                    log.warn("Обнаружен конфликт, выполняется повторная попытка... Попытка № {}", attempt);
                    TimeUnit.MILLISECONDS.sleep(10);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Прервано при повторной попытке транзакции Redis", e);
            }
        }
        if (!updated) {
            log.error("Не удалось обновить просмотры для публикации {} после {} попыток", event.getPostId(), maxRetries);
        }
    }
}
