package faang.school.postservice.properties.user;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "user.ban.redis")
public class UserBanRedisProperties {

    private String channel;

}
