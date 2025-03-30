package faang.school.postservice.config.redis;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "feed")
@Getter
@Setter
public class FeedProperties {
    private int maxSize = 500;
    private int ttlDays = 30;
}
