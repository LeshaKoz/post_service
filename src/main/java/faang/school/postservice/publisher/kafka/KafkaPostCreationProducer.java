package faang.school.postservice.publisher.kafka;

import faang.school.postservice.event.post.PostCreatedEvent;
import faang.school.postservice.model.Post;
import faang.school.postservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaPostCreationProducer {
    private final KafkaTemplate<String, PostCreatedEvent> postCreationEventProducerFactory;
    private final UserService userService;

    public void sendPostPublishedEvent(Post post) {
        PostCreatedEvent event = new PostCreatedEvent();
        event.setAuthorId(post.getAuthorId());
        event.setPostId(post.getId());
        event.setFollowerIds(userService.getFollowerIds(post.getAuthorId()));

        postCreationEventProducerFactory.send("post-creations", event);
    }
}
