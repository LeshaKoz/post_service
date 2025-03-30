package faang.school.postservice.model.cache;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.support.collections.RedisZSet;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@RedisHash("feed")
public class FeedCache {
    @Id
    private long followerId;
    private RedisZSet<Long> postsId;
}
