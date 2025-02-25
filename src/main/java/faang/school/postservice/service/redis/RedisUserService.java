package faang.school.postservice.service.redis;

import faang.school.postservice.model.cache.UserCache;
import faang.school.postservice.repository.cache.RedisUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RedisUserService {
    private final RedisUserRepository redisUserRepository;

    @Value("${spring.data.redis.cache.TTL.user-cache}")
    private long userCacheTTL;

    public void saveUserToCache(UserCache userCache) {
        log.debug("Saving user cache: {}", userCache);
        userCache.setTtl(userCacheTTL);

        redisUserRepository.save(userCache);
    }
}
