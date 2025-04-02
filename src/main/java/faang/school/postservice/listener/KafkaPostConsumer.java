package faang.school.postservice.listener;

import faang.school.postservice.event.kafka.KafkaPostEventDto;
import faang.school.postservice.service.feed.NewsFeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaPostConsumer {

    private final NewsFeedService newsFeedService;

    @KafkaListener(topics = "${spring.data.kafka.topic.posts}", groupId = "${spring.data.kafka.group-id}")
    public void handle(KafkaPostEventDto postEventDto, Acknowledgment ack) {
        try {
            postEventDto.getAuthorFollowersIds().forEach(followerId ->
                    newsFeedService.addPostToFeed(postEventDto, followerId));
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Ошибка обработки События в Kafka: {}", e.getMessage(), e);
        }
    }
}
