package faang.school.postservice.config.redis;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(prefix = "spring.data.redis.cache")
public class CacheProperties {
    private int feedMaxSize;
    private int pageSize;
    private long ttl;
}
