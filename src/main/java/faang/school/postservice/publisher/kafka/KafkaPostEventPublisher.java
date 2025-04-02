package faang.school.postservice.publisher.kafka;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.event.kafka.AbstractKafkaEventDto;
import faang.school.postservice.event.kafka.KafkaPostEventDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class KafkaPostEventPublisher extends AbstractEventPublisher {
    @Value(value = "${spring.data.kafka.keys.post}")
    private String kafkaPostKey;
    private final UserServiceClient userServiceClient;

    public KafkaPostEventPublisher(
            KafkaTemplate<String, AbstractKafkaEventDto> kafkaTemplate,
            @Qualifier("postTopic") NewTopic postTopic,
            UserServiceClient userServiceClient
    ) {
        super(kafkaTemplate, postTopic);
        this.userServiceClient = userServiceClient;
    }

    public void sendPostEvent(KafkaPostEventDto postEvent) {
        List<Long> postAuthorFollowers = fetchPostAuthorFollowers(postEvent.getAuthorId());
        postEvent.setAuthorFollowersIds(postAuthorFollowers);
        sendEvent(postEvent, kafkaPostKey);
    }

    protected List<Long> fetchPostAuthorFollowers(Long authorId) {
        return Optional.ofNullable(userServiceClient.getFollowersIds(authorId))
                .orElseGet(Collections::emptyList);
    }
}

