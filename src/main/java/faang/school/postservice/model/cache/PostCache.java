package faang.school.postservice.model.cache;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(value = "post")
public class PostCache implements Serializable {
    @Id
    private long postId;
    private long authorId;
    private String content;
}
