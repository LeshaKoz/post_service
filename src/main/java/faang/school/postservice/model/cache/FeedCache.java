package faang.school.postservice.model.cache;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.LinkedHashSet;
import java.util.List;

@Setter
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("feed")
public class FeedCache {
    @Id
    private long userId;
    private LinkedHashSet<Long> postIds = new LinkedHashSet<>();

    public List<Long> getPostBatch(int postBatchSize, Long lastPostId) {
        if (lastPostId == null) {
            return postIds.stream()
                    .limit(postBatchSize)
                    .toList();
        }

        return postIds.stream()
                .dropWhile(postId -> postId != lastPostId)
                .skip(1)
                .limit(postBatchSize)
                .toList();
    }
}
