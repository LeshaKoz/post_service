package faang.school.postservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(prefix = "async.thread.pool")
public class AsyncProperties {
    private int poolSize;
    private int queueCapacity;
}
