package faang.school.postservice.model.cache;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@RedisHash("posts")
public class PostCache implements Serializable {
    @Id
    private long postId;
    private long authorId;
    private String content;
}
