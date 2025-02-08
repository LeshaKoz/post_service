package faang.school.postservice.config;

import org.springframework.integration.support.locks.ExpirableLockRegistry;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class RedisLockConfig {

    @Bean
    public ExpirableLockRegistry redisLockRegistry(RedisConnectionFactory redisConnectionFactory) {
        return new RedisLockRegistry(redisConnectionFactory, "post_locks", 60000);
    }
}
