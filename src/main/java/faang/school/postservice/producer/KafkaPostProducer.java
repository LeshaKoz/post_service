package faang.school.postservice.producer;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.kafka.KafkaProducerProperties;
import faang.school.postservice.dto.user.SubscriptionUserDto;
import faang.school.postservice.event.kafka.PostEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class KafkaPostProducer extends AbstractEventProducer<PostEvent> {

    private final KafkaProducerProperties properties;
    private final UserServiceClient userServiceClient;

    public KafkaPostProducer(KafkaTemplate<String, Object> kafkaTemplate,
                             KafkaProducerProperties properties,
                             UserServiceClient userServiceClient) {
        super(kafkaTemplate);
        this.properties = properties;
        this.userServiceClient = userServiceClient;
    }

    public void sendPostCreatedEvent(Long postId, Long authorId) {
        if (!properties.getPosts().getEnabled()) {
            log.warn("Sending post creation events is disabled in settings");
            return;
        }
        List<SubscriptionUserDto> followers = userServiceClient.getFollowers(authorId);

        PostEvent event = PostEvent.builder()
                .postId(postId)
                .authorId(authorId)
                .subscriberIds(followers.stream()
                        .map(SubscriptionUserDto::id)
                        .toList())
                .build();

        send(properties.getPosts().getName(), event);
    }
}
