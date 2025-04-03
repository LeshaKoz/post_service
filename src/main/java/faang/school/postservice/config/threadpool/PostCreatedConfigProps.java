package faang.school.postservice.config.threadpool;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@ConfigurationProperties(prefix = "post-service.post-created.executor")
@Configuration
public class PostCreatedConfigProps {
    int corePoolSize;
    int maxPoolSize;
    int queueCapacity;
}
