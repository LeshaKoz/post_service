package faang.school.postservice.service.event;

import faang.school.postservice.dto.event.CommentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentEventServiceImpl implements CommentEventService {
    private static final String COMMENT_KEY_PREFIX = "post:comments:";

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${feed.comment.max-size}")
    private int commentMaxSize;

    @Override
    public void addCommentToPostToFeed(CommentEvent commentEvent) {
        String commentKey = COMMENT_KEY_PREFIX + commentEvent.postId();
        String lockKey = "lock:" + commentKey;
        String lockValue = UUID.randomUUID().toString();
        try {
            boolean locked = Boolean.TRUE.equals(
                    redisTemplate.opsForValue().setIfAbsent(lockKey, lockValue, 10, TimeUnit.SECONDS));
            if (!locked) {
                log.error("Could not acquire lock for feed: {}", commentKey);
                return;
            }
            if (isCommentAlreadyExists(commentKey, commentEvent.commentId())) {
                return;
            }
            addCommentToPost(commentKey, commentEvent);
            trimComments(commentKey);
        } finally {
            String currentLockValue = redisTemplate.opsForValue().get(lockKey);
            if (lockValue.equals(currentLockValue)) {
                redisTemplate.delete(lockKey);
            }
        }
    }

    public List<CommentEvent> getCachedCommentsForPost(String postId) {
        String postCommentsKey = COMMENT_KEY_PREFIX + postId;

        Set<String> commentIds = redisTemplate.opsForZSet().range(postCommentsKey, 0, -1);

        if (commentIds != null) {
            return commentIds.stream()
                    .map(commentId -> {
                        String commentDataKey = "comment:data:" + commentId;
                        Map<Object, Object> data = redisTemplate.opsForHash().entries(commentDataKey);
                        return new CommentEvent(
                                Long.parseLong((String) data.get("authorId")),
                                Long.parseLong((String) data.get("postId")),
                                Long.parseLong((String) data.get("postAuthorId")),
                                Long.parseLong(commentId),
                                (String) data.get("content"),
                                LocalDateTime.ofInstant(Instant.parse((String) data.get("createdAt")), ZoneId.systemDefault())
                        );
                    })
                    .toList();
        }
        return List.of();
    }

    private boolean isCommentAlreadyExists(String key, long commentId) {
        return redisTemplate.opsForZSet().score(key, String.valueOf(commentId)) != null;
    }

    private void addCommentToPost(String key, CommentEvent event) {
        double score = -event.date().toEpochSecond(ZoneOffset.UTC);
        redisTemplate.opsForZSet().add(key, String.valueOf(event.commentId()), score);

        String commentDataKey = "comment:data:" + event.commentId();
        redisTemplate.opsForHash().put(commentDataKey, "authorId", event.authorId());
        redisTemplate.opsForHash().put(commentDataKey, "content", event.content());
        redisTemplate.opsForHash().put(commentDataKey, "createdAt", event.date().toString());
    }

    private void trimComments(String key) {
        redisTemplate.opsForZSet().removeRange(key, commentMaxSize, -1);
    }
}
