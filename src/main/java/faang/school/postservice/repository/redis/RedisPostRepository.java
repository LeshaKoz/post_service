package faang.school.postservice.repository.redis;

import faang.school.postservice.dto.post.PostRedisDto;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RedisPostRepository {
    @Value("${news-feed.keys.post}")
    private String POSTS_KEY;

    @Value("${news-feed.ttl}")
    private int ttl;

    private final RedisTemplate<String, PostRedisDto> redisTemplate;

    public RedisPostRepository(
        @Qualifier("postRedis") RedisTemplate<String, PostRedisDto> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void savePost(PostRedisDto post) {
        redisTemplate.opsForValue().set(prefixPost(post.getId()), post, ttl, TimeUnit.SECONDS);
    }

    public void cachePosts(List<PostRedisDto> posts) {
        posts.forEach(this::savePost);
    }

    public List<PostRedisDto> getPosts(List<Long> postIds) {
        List<String> redisPostKeys = postIds.stream()
            .map(this::prefixPost)
            .collect(Collectors.toList());

        return redisTemplate.opsForValue().multiGet(redisPostKeys).stream()
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    public PostRedisDto findPostById(Long postId) {
        return redisTemplate.opsForValue().get(prefixPost(postId));
    }

    private String prefixPost(Long postId) {
        return String.format("%s:%d", POSTS_KEY, postId);
    }
}
