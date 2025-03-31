package faang.school.postservice.repository;

import faang.school.postservice.config.redis.CacheProperties;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.dto.user.UserResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RedisUserRepository {
    private final RedisTemplate<String, Object> cacheRedisTemplate;
    private final CacheProperties properties;
    private static final String USER_KEY_PREFIX = "user:";

    public void save(UserResponseDto userDto) {
        String key = USER_KEY_PREFIX + userDto.getId();
        cacheRedisTemplate.opsForValue().set(key, userDto, Duration.ofSeconds(properties.getTtl()));
        log.info("userDto was saved. key {} userDto {}", key, userDto);
    }

    public UserDto get(Long userId) {
        String key = USER_KEY_PREFIX + userId;
        log.info("get userDto for userId. key {} userId {}", key, userId);
        return (UserDto) cacheRedisTemplate.opsForValue().get(key);
    }
}
