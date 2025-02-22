package faang.school.postservice.consumer;

import faang.school.postservice.dto.event.PostEventDto;
import faang.school.postservice.service.feed.FeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaPostConsumer {

  private final FeedService feedService;

  @KafkaListener(topics = "post_topic", containerFactory = "kafkaListenerContainerFactory", groupId = "feed-group-id")
  public void consumePublishedPostEvent(PostEventDto dto, Acknowledgment acknowledgment) {

    log.info("MESSAGE SUCCESSFULLY RECEIVED BY CONSUMER. POST ID = {}", dto.getPosId());
    log.info("FOLLOWERS FEEDS TO Update {}", dto.getFollowers());

    feedService.processPostEvent(dto);
    // надо почитать/посмотреть, получается это в отдельном потоке по идее обрабатывается
    // соответственно параллельно разные посты в фид одного юзера прилетают
    // думаю достаточно синхронизацию сделать для фида пользователя (?)

    acknowledgment.acknowledge();
  }

}
