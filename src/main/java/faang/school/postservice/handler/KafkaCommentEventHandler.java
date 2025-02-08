package faang.school.postservice.handler;

import faang.school.postservice.dto.CommentEvent;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.mapper.PostRedisMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.PostRedis;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRedisRepository;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.support.locks.ExpirableLockRegistry;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.locks.Lock;

@Slf4j
@Component
@KafkaListener(
        topics = {"${spring.data.kafka.topics.comments.name:default-topic}"},
        groupId = "${spring.kafka.consumer.group-id:default-group}",
        concurrency = "${spring.kafka.consumer.concurrency}")
@RequiredArgsConstructor
public class KafkaCommentEventHandler {

    private final CommentRepository commentRepository;
    private final PostRedisRepository postRedisRepository;
    private final PostRepository postRepository;
    private final CommentMapper commentMapper;
    private final PostRedisMapper postRedisMapper;
    private final ExpirableLockRegistry redisLockRegistry;

    @Value("${spring.data.redis.comment-max-size}")
    private int commentsMaxSize;

    @Transactional(readOnly = true)
    @KafkaHandler
    public void handle(CommentEvent commentEvent, Acknowledgment acknowledgment) {
        Long commentId = commentEvent.getCommentId();
        Long postId = commentEvent.getPostId();
        log.info("Received event CommentHandlerResponse: {} {}", commentId, commentEvent);

        String lockKey = "post:" + postId;
        Lock lock = redisLockRegistry.obtain(lockKey);

        if (!lock.tryLock()) {
            log.warn("Failed to obtain lock for post {}. Skipping update.", postId);
            return;
        }

        try {
            Comment comment = commentRepository.findById(commentId)
                    .orElseThrow(() -> new NoSuchElementException("Comment with id {" + commentId + "} not found"));
            CommentDto commentDto = commentMapper.toDto(comment);

            PostRedis postRedis = postRedisRepository.findById(postId).orElse(null);
            if (postRedis == null) {
                Post post = postRepository.findById(postId)
                        .orElseThrow(() -> new NoSuchElementException("Post with id {" + postId + "} not found"));
                postRedis = postRedisMapper.toPostCache(post);
            }

            addComment(postRedis, commentDto);
            log.info("Comment with id:{} is successfully added to post.", commentId);

            postRedisRepository.save(postRedis);
            acknowledgment.acknowledge();

            log.info("Comment {} successfully added to post {}", commentId, postId);
        } catch (Exception e) {
            log.error("Error processing event for post {}: {}", postId, e.getMessage());
        } finally {
            lock.unlock();
        }
    }

    public void addComment(PostRedis postRedis, CommentDto commentDto) {
        Queue<Long> comments = postRedis.getCommentsIds() != null
                ? new ArrayDeque<>(postRedis.getCommentsIds())
                : new ArrayDeque<>();

        if (comments.size() >= commentsMaxSize) {
            comments.poll();
        }

        comments.offer(commentDto.getId());

        postRedis.setCommentsIds(new ArrayList<>(comments));
    }
}


