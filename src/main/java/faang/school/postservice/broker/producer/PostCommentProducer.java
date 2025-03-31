package faang.school.postservice.broker.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.kafka.CustomKafkaProperties;
import faang.school.postservice.dto.comment.PostCommentEvent;
import faang.school.postservice.model.Comment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PostCommentProducer extends KafkaProducerService{
    private final CustomKafkaProperties customKafkaProperties;

    public PostCommentProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper, CustomKafkaProperties customKafkaProperties) {
        super(kafkaTemplate, objectMapper);
        this.customKafkaProperties = customKafkaProperties;
    }

    @Async("asyncTaskExecutor")
    public void produceCommentPostEventAsync(Comment comment) {
        produceCommentPostEvent(comment);
    }
    public void produceCommentPostEvent(Comment comment) {

        long postId = comment.getPost().getId();
        PostCommentEvent postCommentEvent = PostCommentEvent.builder()
                .postId(postId)
                .commentId(comment.getId())
                .authorId(comment.getAuthorId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .build();

        super.sendPostMessage(customKafkaProperties.topic().postCommentsTopic(), postCommentEvent);
        log.info("Sending postCommentEvent to message broker. Post : {}", postId);
    }

}
