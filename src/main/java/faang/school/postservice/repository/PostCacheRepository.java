package faang.school.postservice.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.model.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Repository
public class PostCacheRepository {
    private final RedisTemplate<String, Object> postRedisTemplate;
    private final ObjectMapper objectMapper;
    @Value("${spring.data.redis.post-ttl-hours}")
    private int ttlInHours;
    @Value("${spring.data.redis.post-ttl-key}")
    private String postTtlKey;
    @Value("${spring.data.redis.post-id-key}")
    private String postKey;


    public void save(Post post) {
        double expireTime = System.currentTimeMillis() + (ttlInHours * 60 * 60 * 1000L);
        postRedisTemplate.opsForZSet().add(postTtlKey, post.getId(), expireTime);
        postRedisTemplate.opsForHash().put(postKey, post.getId(), post);
    }

    public void saveAll(List<Post> postList) {
        postList.forEach(this::save);
    }

    @Nullable
    public Post findById(Long id) {
        Object object = postRedisTemplate.opsForHash().get(postKey, id);
        return objectMapper.convertValue(object, Post.class);
    }

    public void clearCache() {
        long currentTimeStamp = System.currentTimeMillis();
        Set<Long> postByCleared = Objects.requireNonNull(postRedisTemplate.opsForZSet()
                        .range(postTtlKey, 0, currentTimeStamp))
                .stream()
                .map(post -> ((Post) post).getId())
                .collect(Collectors.toUnmodifiableSet());

        postRedisTemplate.opsForHash().delete(postKey, postByCleared);
        postRedisTemplate.opsForZSet().removeRange(postTtlKey, 0, currentTimeStamp);
    }

    public void deletePost(Long id) {
        postRedisTemplate.opsForZSet().remove(postTtlKey, id);
        postRedisTemplate.opsForHash().delete(postKey, id);
    }
}
