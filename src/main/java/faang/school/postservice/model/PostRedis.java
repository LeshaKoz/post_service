package faang.school.postservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.List;

@RedisHash(value = "posts", timeToLive = 86400)
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PostRedis implements Serializable {
    @Id
    private Long id;
    private Long authorId;
    private Long projectId;
    private String content;
    private List<Long> likesIds;
    private List<Long> commentsIds;
}
