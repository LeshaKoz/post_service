package faang.school.postservice.repository.redis;

import faang.school.postservice.dto.post.PostRedisDto;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RedisPostRepository {
    @Value("${news-feed.keys.post}")
    private String POSTS_KEY;

    private final RedisTemplate<String, Object> redisTemplate;
    private final HashOperations<String, String, PostRedisDto> hashOperationsPost;

    public RedisPostRepository(
        @Qualifier("postRedis") HashOperations<String, String, PostRedisDto> hashOperationsPost,
        @Qualifier("feedRedis") RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.hashOperationsPost = hashOperationsPost;
    }

    public List<PostRedisDto> getPosts(List<Long> postIds) {
        List<String> post = postIds.stream()
            .map(String::valueOf)
            .toList();
        return hashOperationsPost.multiGet(POSTS_KEY, post);
    }

    public void cachePosts(List<PostRedisDto> posts) {
        posts.forEach(post -> {
            hashOperationsPost.putAll(POSTS_KEY, Map.of(post.getId().toString(), post));
            redisTemplate.expire(POSTS_KEY, 7, TimeUnit.DAYS);
        });
    }
}
