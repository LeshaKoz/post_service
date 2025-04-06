package faang.school.postservice.model.cache;

import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.support.collections.RedisZSet;

@Builder
@RedisHash("feed")
public class FeedCache {
    @Id
    private long followerId;
    private RedisZSet<Long> postIds;
}
