package faang.school.postservice.publisher.kafka;

import faang.school.postservice.event.post.PostDeletedEvent;
import faang.school.postservice.model.Post;
import faang.school.postservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaPostDeleteProducer {
    private final KafkaTemplate<String, PostDeletedEvent> postDeletedEventKafkaTemplate;
    private final UserService userService;

    public void sendPostDeletedEvent(Post post) {
        PostDeletedEvent event = new PostDeletedEvent();
        event.setPostId(post.getId());
        event.setFollowerIds(userService.getFollowerIds(post.getAuthorId()));

        postDeletedEventKafkaTemplate.send("post-deletions", event);
    }
}
