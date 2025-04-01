package faang.school.postservice.service.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.user.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthorCacheServiceImpl implements AuthorCacheService {

    private static final String AUTHOR_CACHE_KEY = "authors";

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${redis.cache.authors-ttl}")
    private long authorTtl;

    public void cacheAuthor(long postId, UserDto author) {
        String authorJson = mapObjectToString(author);
        redisTemplate.opsForHash().put(AUTHOR_CACHE_KEY, postId, authorJson);
        redisTemplate.expire(AUTHOR_CACHE_KEY, authorTtl, TimeUnit.SECONDS);
    }

    public Optional<UserDto> getCachedAuthor(Long authorId) {
        Object authorJson = redisTemplate.opsForHash().get(AUTHOR_CACHE_KEY, authorId.toString());
        if (authorJson == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(mapObjectToUser(authorJson));
    }

    private String mapObjectToString(UserDto author) {
        try {
            return objectMapper.writeValueAsString(author);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize for caching", e);
            throw new RuntimeException(e);
        }
    }

    private UserDto mapObjectToUser(Object json) {
        try {
            return objectMapper.readValue(json.toString(), UserDto.class);
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize for caching", e);
            throw new RuntimeException(e);
        }
    }
}