package faang.school.postservice.repository.rediscacherepository;


import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class CommentCacheRepository {

    private static final String USER_FEED_KEY_PREFIX = "user_feed:";
    private static final String COMMENT_AUTHOR_KEY_PREFIX = "comment_author:";
    private final RedisTemplate<String, CommentDto> redisTemplate;
    private final RedisTemplate<String, UserDto> userRedisTemplate;
    private final ZSetOperations<String, CommentDto> zSetOperations;

    @Value("${redis.comment.ttl}")
    private long ttl;

    public void saveComment(long userId, CommentDto commentDto) {
        long timestamp = System.currentTimeMillis();
        String userFeedKey = USER_FEED_KEY_PREFIX + userId;
        zSetOperations.add(userFeedKey, commentDto, timestamp);
        redisTemplate.expire(userFeedKey, ttl, TimeUnit.SECONDS);
    }

    public Set<CommentDto> getRecentComments(long userId) {
        String userFeedKey = USER_FEED_KEY_PREFIX + userId;
        return zSetOperations.reverseRange(userFeedKey, 0, -1);
    }

    public void saveAuthor(long commentId, UserDto author) {
        String authorKey = COMMENT_AUTHOR_KEY_PREFIX + commentId;
        userRedisTemplate.opsForValue().set(authorKey, author, ttl, TimeUnit.SECONDS);
    }

    public UserDto getAuthor(long commentId) {
        String authorKey = COMMENT_AUTHOR_KEY_PREFIX + commentId;
        return (UserDto) userRedisTemplate.opsForValue().get(authorKey);
    }

}
