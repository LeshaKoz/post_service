package faang.school.postservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "spring.kafka.topics")
public class FeedProperties {
    private Post post;
    private PostDelete postDelete;

    @Getter
    @Setter
    public static class Post {
        private String name;
        private int subscribersBatchSize;
    }

    @Getter
    @Setter
    public static class PostDelete {
        private String name;
    }
}
