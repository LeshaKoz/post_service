package faang.school.postservice.model.cache;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.util.List;

@RedisHash(value = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCache {
    @Id
    private Long id;
    private String username;
    private List<Long> followerIds;
    private List<Long> followeeIds;

    @TimeToLive
    private long ttl;
}
