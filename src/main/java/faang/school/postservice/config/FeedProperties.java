package faang.school.postservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "spring.kafka.topics.post")
public class FeedProperties {
    private int subscribersBatchSize;
    private String name;
}
