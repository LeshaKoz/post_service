package faang.school.postservice.repository.cache;

import faang.school.postservice.dto.post.PostReadDto;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class RedisFeedRepository {

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisTemplate<String, Long> redisTemplateLong;

    @Value("${spring.data.redis.feed-cache.key}")
    private String key;

    @Value("${spring.data.redis.feed-cache.max-feed-size:500}")
    private long maxFeedSize;

    @Value("${spring.data.redis.zset.range.remove.start.index:0}")
    private int zSetRemoveRangeStartIndex;

    @Value("${spring.data.redis.zset.range.remove.end.index:0}")
    private int zSetRemoveRangeEndIndex;

    @Value("${spring.data.redis.zset.range.offset.index:1}")
    private int zSetOffsetRange;

    private ZSetOperations<String, Object> opsForZSet;

    @PostConstruct
    private void init() {
        opsForZSet = redisTemplate.opsForZSet();
    }

    public void add(long subscriberId, long postId) {
        opsForZSet.add(key + subscriberId, postId, System.currentTimeMillis());
    }

    public void saveAll(long userId, List<PostReadDto> posts) {
        Set<ZSetOperations.TypedTuple<Long>> tuples = posts.stream()
                .map(post -> ZSetOperations.TypedTuple.of(
                        post.getId(),
                        (double) System.currentTimeMillis()))
                .collect(Collectors.toSet());

        redisTemplateLong.opsForZSet().add(key + userId, tuples);
    }

    public Set<Long> findPostsId(long subscriberId) {
        Set<Object> posts = opsForZSet.reverseRange(
                key + subscriberId,
                zSetRemoveRangeStartIndex,
                maxFeedSize - zSetOffsetRange);
        if (posts != null) {
            return posts.stream()
                    .map(post -> Long.valueOf(String.valueOf(post)))
                    .collect(Collectors.toSet());
        }

        return Collections.emptySet();
    }

    public void checkMaxFeedSize(Set<Long> postsId, long subscriberId) {
        if (postsId != null && postsId.size() >= maxFeedSize) {
            opsForZSet.removeRange(
                    key + subscriberId,
                    zSetRemoveRangeStartIndex,
                    zSetRemoveRangeEndIndex);
        }
    }

    public Long getRank(long id, long postId) {
        return opsForZSet.rank(key + id, postId);
    }

    public Set<Object> getRange(long id, long startPostId, long endPostId) {
        return opsForZSet.range(key + id, startPostId, endPostId);
    }
}
