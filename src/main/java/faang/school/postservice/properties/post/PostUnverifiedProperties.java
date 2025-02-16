package faang.school.postservice.properties.post;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "post.unverified")
public class PostUnverifiedProperties {

    private int max;

}