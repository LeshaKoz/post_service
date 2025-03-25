package faang.school.postservice.config.redis;

import faang.school.postservice.properties.RedisCacheProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Configuration
public class CacheConfig {

    private final RedisCacheProperties redisCacheProperties;

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        cacheConfigurations.put("posts",
                RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.ofDays(redisCacheProperties.getPostTtl())));
        cacheConfigurations.put("authors",
                RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.ofDays(redisCacheProperties.getAuthorsTtl())));
        cacheConfigurations.put("postsByHashtag",
                RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.ofMinutes(redisCacheProperties.getHashtagTtl())));
        return  RedisCacheManager
                .builder(redisConnectionFactory)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }
}