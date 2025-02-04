package faang.school.postservice.consumer;

import faang.school.postservice.dto.event.PostEventDto;
import faang.school.postservice.service.feed.FeedService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Listener {

  private final FeedService feedService;

  private final Logger logger =
      LoggerFactory.getLogger(this.getClass());

  @KafkaListener(topics = "post_topic", containerFactory = "kafkaListenerContainerFactory", groupId = "feed-group-id")
  public void handle(PostEventDto dto, Acknowledgment acknowledgment) {

    logger.info("MESSAGE SUCCESSFULLY RECEIVED BY CONSUMER. BUT NOT PROCESSED YET");
    logger.info("message received {}", dto.getFollowers());

    feedService.processPostEvent(dto);

    acknowledgment.acknowledge();
  }

}
