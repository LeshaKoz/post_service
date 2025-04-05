package faang.school.postservice.service.feed;

import faang.school.postservice.dto.user.UserRedisDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
public class UserRedisDtoCashingService {

    @Value("${cache.userAuthorTtl}")
    private int userAuthorTtl;
    @Value("${news-feed.keys.user}")
    private String USER_KEY;
    private final RedisTemplate<String, UserRedisDto> redisTemplate;

    public void cacheAuthorByComment(UserRedisDto userRedisDto) {

        HashOperations<String, String, UserRedisDto> hashOps = redisTemplate.opsForHash();
        hashOps.put(USER_KEY, userRedisDto.id().toString(), userRedisDto);
        redisTemplate.expire(USER_KEY, userAuthorTtl, TimeUnit.SECONDS);

    }
}
