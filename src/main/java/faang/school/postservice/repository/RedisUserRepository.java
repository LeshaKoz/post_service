package faang.school.postservice.repository;

import faang.school.postservice.config.redis.CacheProperties;
import faang.school.postservice.dto.user.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class RedisUserRepository {
    private final RedisTemplate<String, Object> cacheRedisTemplate;
    private final CacheProperties properties;
    private static final String USER_KEY_PREFIX = "user:";

    public void save(UserDto userDto) {
        String key = USER_KEY_PREFIX + userDto.id();
        cacheRedisTemplate.opsForValue().set(key, userDto, Duration.ofSeconds(properties.getTtl()));
    }

    public void save(List<UserDto> userDtos) {
        userDtos.forEach(this::save);
    }

    public UserDto get(Long userId) {
        String key = USER_KEY_PREFIX + userId;
        return (UserDto) cacheRedisTemplate.opsForValue().get(key);
    }

    public List<UserDto> multiGet(Set<Long> userIds) {
        List<String> keys = userIds.stream()
                .map(userId -> USER_KEY_PREFIX + userId)
                .toList();

        List<Object> userDtos = cacheRedisTemplate.opsForValue().multiGet(keys);

        if (userDtos == null) {
            return Collections.emptyList();
        }

        return userDtos.stream()
                .filter(Objects::nonNull)
                .map(UserDto.class::cast)
                .toList();
    }
}
