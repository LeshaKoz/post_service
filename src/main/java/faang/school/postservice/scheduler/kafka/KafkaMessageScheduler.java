package faang.school.postservice.scheduler.kafka;

import faang.school.postservice.service.kafka.KafkaMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaMessageScheduler {

    private final KafkaMessageService kafkaMessageService;

    @Scheduled(fixedRateString = "${kafka.message.send.interval}")
    @SchedulerLock(name = "sendKafkaMessages")
    public void sendKafkaMessages() {
        kafkaMessageService.sendMessages();
    }
}