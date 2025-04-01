package faang.school.postservice.service.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.post.PostResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostCacheServiceImpl implements PostCacheService {

    private static final String POST_CACHE_PREFIX = "post:";
    private static final String USER_FEED_CACHE_PREFIX = "user:feed:";

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${redis.cache.post-ttl}")
    private long postTtl;

    public void cachePost(PostResponseDto post) {
        String json;
        try {
            json = objectMapper.writeValueAsString(post);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        redisTemplate.opsForValue().set(POST_CACHE_PREFIX + post.id(), json, postTtl);
    }

    public Optional<PostResponseDto> getCachedPost(long postId) {
        try {
            Optional<String> post = Optional.ofNullable(redisTemplate.opsForValue().get(POST_CACHE_PREFIX + postId));
            if (post.isPresent()) {
                return Optional.ofNullable(objectMapper.readValue(post.get(), PostResponseDto.class));
            }
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize cached post with id: {}", postId, e);
            redisTemplate.delete(String.format("%s::%d", "posts", postId));
            log.error("Invalid cache post deleted");
        }
        return Optional.empty();
    }

    @Override
    public List<PostResponseDto> getCachedPosts(List<String> authorIds, int limit) {
        List<PostResponseDto> cachedPosts = new ArrayList<>();

        for (String authorId : authorIds) {
            String feedKey = USER_FEED_CACHE_PREFIX + authorId;
            Set<String> postIds = redisTemplate.opsForZSet().reverseRange(feedKey, 0, limit - 1);

            if (postIds != null) {
                postIds.forEach(postId -> {
                    String postKey = POST_CACHE_PREFIX + postId;
                    Map<Object, Object> postData = redisTemplate.opsForHash().entries(postKey);
                    if (!postData.isEmpty()) {
                        cachedPosts.add(mapToPost(postId, postData));
                    }
                });
            }
        }

        cachedPosts.sort((p1, p2) -> p2.createdAt().compareTo(p1.createdAt()));
        return cachedPosts;
    }

    @Override
    public void cachePosts(List<PostResponseDto> posts) {
        posts.forEach(post -> {
            String postKey = POST_CACHE_PREFIX + post.id();
            redisTemplate.opsForHash().putAll(postKey, postToMap(post));
            redisTemplate.expire(postKey, 1, TimeUnit.DAYS);
            String feedKey = USER_FEED_CACHE_PREFIX + post.authorId();
            redisTemplate.opsForZSet().add(feedKey, String.valueOf(post.id()), -post.createdAt().toEpochSecond(ZoneOffset.UTC));
            redisTemplate.expire(feedKey, 1, TimeUnit.DAYS);
        });
    }

    private Map<String, Object> postToMap(PostResponseDto post) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", post.id());
        map.put("authorId", post.authorId());
        map.put("content", post.content());
        map.put("createdAt", post.createdAt().toString());
        map.put("likesCount", String.valueOf(post.likesCount()));
        map.put("commentsCount", String.valueOf(post.commentsCount()));
        map.put("viewsCount", String.valueOf(post.viewsCount()));
        return map;
    }

    private PostResponseDto mapToPost(String postId, Map<Object, Object> map) {
        return PostResponseDto.builder()
                .id(Long.parseLong(postId))
                .authorId(Long.parseLong((String) map.get("authorId")))
                .content((String) map.get("content"))
                .createdAt(LocalDateTime.from(Instant.parse((String) map.get("createdAt"))))
                .likesCount(Integer.parseInt((String) map.get("likesCount")))
                .commentsCount(Integer.parseInt((String) map.get("commentsCount")))
                .viewsCount(Integer.parseInt((String) map.get("viewsCount")))
                .build();
    }

}
