package faang.school.postservice.broker.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.config.kafka.CustomKafkaProperties;
import faang.school.postservice.dto.post.PostPublicationEvent;
import faang.school.postservice.dto.subscription.SubscriptionUserDto;
import faang.school.postservice.mapper.user.UserDtoAdapter;
import faang.school.postservice.model.Post;
import faang.school.postservice.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class PostEventProducer extends KafkaProducerService {

    private final CustomKafkaProperties customKafkaProperties;
    private final UserService userService;
    private final UserDtoAdapter userDtoAdapter;
    private final UserContext userContext;

    public PostEventProducer(KafkaTemplate<String, String> kafkaTemplate,
                             CustomKafkaProperties customKafkaProperties,
                             ObjectMapper objectMapper, UserService userService,
                             UserDtoAdapter userDtoAdapter, UserContext userContext) {
        super(kafkaTemplate, objectMapper);
        this.customKafkaProperties = customKafkaProperties;
        this.userService = userService;
        this.userDtoAdapter = userDtoAdapter;
        this.userContext = userContext;
    }

    @Async("asyncTaskExecutor")
    public void producePublishPostEventAsync(long userId, Post post) {
        userContext.setUserId(userId);
        producePublishPostEvent(post);
    }

    public void producePublishPostEvent(Post post) {
        List<SubscriptionUserDto> followers = userService.getFollowers(post.getAuthorId());
        List<Long> followersIds = followers.stream()
                .map(SubscriptionUserDto::id)
                .toList();
        producePublishPostEventToFollowers(post.getId(), followersIds);
    }

    private void producePublishPostEventToFollowers(long postId, List<Long> followersIds) {
        PostPublicationEvent postPublicationEvent = PostPublicationEvent.builder()
                .postId(postId)
                .followersIds(followersIds)
                .build();
        super.sendPostMessage(customKafkaProperties.topic().postsTopic(), postPublicationEvent);
        log.info("Sending PublishPostEvent to message broker. Post : {}", postId);
    }
}
