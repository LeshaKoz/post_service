package faang.school.postservice.consumer;

import faang.school.postservice.event.PostEvent;
import faang.school.postservice.exception.SubscriberProcessingException;
import faang.school.postservice.exception.InvalidPostEventException;
import faang.school.postservice.service.FeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaPostConsumer {

    private final FeedService feedService;

    @KafkaListener(topics = "posts", groupId = "feed-group")
    public void listen(PostEvent postEvent) {
        validatePostEvent(postEvent);
        List<Long> failedSubscribers = postEvent.subscriberIds().stream()
                .filter(subscriberId -> !processSubscriber(subscriberId, postEvent))
                .toList();

        checkFailedSubscribers(failedSubscribers);
    }

    private void checkFailedSubscribers(List<Long> failedSubscribers) {
        if (!failedSubscribers.isEmpty()) {
            throw new SubscriberProcessingException("Не удалось обработать подписчиков: " + failedSubscribers);
        }
    }

    private void validatePostEvent(PostEvent postEvent) {
        if (postEvent == null || postEvent.subscriberIds() == null) {
            throw new InvalidPostEventException("Ивент или ID подписчика не может быть null");
        }
    }

    private boolean processSubscriber(long subscriberId, PostEvent postEvent) {
        try {
            feedService.addPostToFeed(subscriberId, postEvent);
            return true;
        } catch (Exception e) {
            log.error("Ошибка при обработке ID подписчика: {}", subscriberId, e);
            return false;
        }
    }
}