package faang.school.postservice.repository.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.event.PostEventDto;
import faang.school.postservice.exception.RedisTransactionException;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Repository;

@Slf4j
@RequiredArgsConstructor
@Repository
public class FeedCacheRepository {

  @Value("${newsfeed.feed.prefix}")
  private String feedPrefix;

  @Value("${newsfeed.posts.limit:500}")
  private int postsLimit;

  private final RedisTemplate<String, Object> redisTemplate;
  private final ObjectMapper objectMapper;

  @SuppressWarnings("unchecked")
  @Retryable(retryFor = RedisTransactionException.class, backoff = @Backoff(delay = 3000L))
  public void executeWithOptimisticLock(Runnable task, String key) {
    var operation = new SessionCallback<>() {
      public List<Object> execute(RedisOperations operations) {
        operations.watch(key);
        operations.multi();

        task.run();

        List<Object> result = operations.exec();
        operations.unwatch();

        if (result.isEmpty()) {
          operations.discard();
          throw new RedisTransactionException("Failed Redis Transaction");
        }
        return result;
      }
    };

    redisTemplate.execute(operation);
  }

  public void addPost(Long userId, PostEventDto dto) {
    String feedKey = getFeedKey(userId);
    double score = dto.getUpdatedAt().toEpochSecond(ZoneOffset.UTC);
    redisTemplate.opsForZSet().addIfAbsent(feedKey, dto.getPosId(), score);
    redisTemplate.opsForZSet().removeRange(feedKey, 0, -postsLimit - 1);
  }

  public List<Long> findPostsByUserId(Long userId, Long previousPostId, int pageSize) {
    Long previousPostRank;
    if (previousPostId == null) {
      previousPostRank = -1L;
    } else {
      previousPostRank = redisTemplate.opsForZSet().reverseRank(getFeedKey(userId), previousPostId);
    }

    if (previousPostRank == null) {
      log.info("No previous postId found");
      return Collections.emptyList();
    }
    long start = previousPostRank + 1;
    long end = previousPostRank + pageSize;

    return Objects.requireNonNull(
            redisTemplate.opsForZSet().reverseRange(getFeedKey(userId), start, end))
        .stream()
        .map(value -> objectMapper.convertValue(value, Long.class))
        .toList();
  }

  private String getFeedKey(Long userId) {
    return feedPrefix + userId;
  }
}
