package faang.school.postservice.repository.redis;

import faang.school.postservice.dto.user.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class RedisUserRepository {
    private static final String USER_KEY_TEMPLATE = "user:%d";

    private final RedisTemplate<String, UserDto> userDtoRedisTemplate;

    public void save(UserDto userDto, Duration userTtl) {
        String key = formatUserKey(userDto.id());
        userDtoRedisTemplate.opsForValue().set(key, userDto, userTtl);
    }

    private String formatUserKey(Long userId) {
        return String.format(USER_KEY_TEMPLATE, userId);
    }
}
