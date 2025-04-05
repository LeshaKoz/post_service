package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.repository.redis.RedisUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserCashService {
    private final RedisUserRepository redisUserRepository;
    private final UserServiceClient userServiceClient;

    @Value("${app.newsfeed.cache.user-ttl}")
    private Duration userTtl;

    public void cacheUser(Long userId) {
        UserDto userDto = userServiceClient.getUser(userId);

        if (userDto == null) {
            log.warn("User with ID {} not found. Caching skipped.", userId);
            return;
        }

        redisUserRepository.save(userDto, userTtl);
        log.info("User {} cached successfully. TTL: {}", userId, userTtl);
    }
}
