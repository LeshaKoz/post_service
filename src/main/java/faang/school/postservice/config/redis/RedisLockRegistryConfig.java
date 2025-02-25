package faang.school.postservice.config.redis;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.integration.support.locks.ExpirableLockRegistry;

@Configuration
public class RedisLockRegistryConfig {

    @Value("${spring.data.redis.lock-registry.key}")
    private String lockRegistryKey;

    @Bean
    public ExpirableLockRegistry redisLockRegistry(RedisConnectionFactory redisConnectionFactory) {
        return new RedisLockRegistry(redisConnectionFactory, lockRegistryKey);
    }
}
