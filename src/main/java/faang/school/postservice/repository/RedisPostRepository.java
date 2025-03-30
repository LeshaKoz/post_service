package faang.school.postservice.repository;

import faang.school.postservice.config.redis.CacheProperties;
import faang.school.postservice.dto.post.PostResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import java.time.Duration;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RedisPostRepository {
    private final RedisTemplate<String, Object> cacheRedisTemplate;
    private final CacheProperties properties;
    private static final String POST_KEY_PREFIX = "post:";

    public void addNewPost(PostResponseDto postDto) {
        Long postId = postDto.id();
        String key = POST_KEY_PREFIX + postId;
        cacheRedisTemplate.opsForValue().set(key, postDto, Duration.ofSeconds(properties.getTtl()));
        log.info("postDto was added to cache: key {} postDto {}", key, postDto);
    }

    public Optional<PostResponseDto> getPost(Long postId) {
        String key = POST_KEY_PREFIX + postId;
        PostResponseDto postDto = (PostResponseDto) cacheRedisTemplate.opsForValue().get(key);
        log.info("postDto was read from cache: key {} postDto {}", key, postDto);
        return Optional.ofNullable(postDto);
    }

    public void deletePost(Long postId) {
        String key = POST_KEY_PREFIX + postId;
        cacheRedisTemplate.delete(key);
        log.info("Post was deleted. key {} postId {} ", key, postId);
    }
}
