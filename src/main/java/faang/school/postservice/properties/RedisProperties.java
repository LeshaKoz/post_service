package faang.school.postservice.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
@ConfigurationProperties(prefix = "redis.cache")
public class RedisProperties {

    private int ttlMinutes;
}