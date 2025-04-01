package faang.school.postservice.kafka.listener;

import faang.school.postservice.dto.feed.HeatTask;
import faang.school.postservice.service.feed.FeedHeaterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaFeedHeatingListener {

    private final FeedHeaterService feedHeaterService;

    @KafkaListener(topics = "kafka.feed.heat-tasks.topic", groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaManualAckListenerContainerFactory")
    public void processHeatingBatch(HeatTask task) {
        log.info("Start heating cache...");
        feedHeaterService.cacheHeat(task);
        log.info("Heating cache done");
    }
}