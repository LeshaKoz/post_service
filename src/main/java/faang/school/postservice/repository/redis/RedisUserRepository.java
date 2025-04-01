package faang.school.postservice.repository.redis;

import faang.school.postservice.dto.user.UserRedisDto;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RedisUserRepository {
    @Value("${news-feed.keys.user}")
    private String USER_KEY;

    private final RedisTemplate<String, UserRedisDto> redisTemplate;

    @Value("${news-feed.ttl}")
    private int ttl;

    public RedisUserRepository(
        @Qualifier("userRedis") RedisTemplate<String, UserRedisDto> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean checkUserExist(Long userId) {
        return redisTemplate.opsForValue().get(prefixUser(userId)) != null;
    }

    public List<UserRedisDto> getUsers(List<Long> authorIds) {
        List<String> authors = authorIds.stream()
            .map(this::prefixUser)
            .collect(Collectors.toList());
        return redisTemplate.opsForValue().multiGet(authors);
    }

    public void putUsers(List<UserRedisDto> users) {
        users.forEach(user -> {
            redisTemplate.opsForValue()
                .set(prefixUser(user.id()), user, ttl, TimeUnit.SECONDS);
        });
    }

    private String prefixUser(Long postId) {
        return String.format("%s:%d", USER_KEY, postId);
    }
}
