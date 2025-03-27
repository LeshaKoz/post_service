package faang.school.postservice.dto.user;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

@RedisHash("Authors")
@Data
@Builder
public class PostAuthorCacheDto implements Serializable {
    private Long id;
    private String username;
    private String email;

    @TimeToLive(unit = TimeUnit.HOURS)
    private int hoursToExpire;
}
