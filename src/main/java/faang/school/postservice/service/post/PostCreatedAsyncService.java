package faang.school.postservice.service.post;

import faang.school.postservice.dto.post.PostCreatedEvent;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class PostCreatedAsyncService {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final PostRepository postRepository;

    @Value("${kafka.topic.post}")
    String topicName;

    @Value("${post.subscribers.chunk-size}")
    int subscribersChunkSize;

    @Async
    public void processPostCreated(Post result) {
        List<Long> subscribersIds = postRepository.findFollowerIdsByFolloweeId(result.getAuthorId());
        List<List<Long>> partitioned = ListUtils.partition(subscribersIds, subscribersChunkSize);
        partitioned.forEach(list -> {
            PostCreatedEvent createdEvent = new PostCreatedEvent(
                    result.getId(), result.getAuthorId(), list);
            kafkaTemplate.send(topicName, result.getId().toString(), createdEvent);
        });

    }
}
