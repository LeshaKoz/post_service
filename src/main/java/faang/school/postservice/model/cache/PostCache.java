package faang.school.postservice.model.cache;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("post")
public class PostCache {
    @Id
    private long id;
    private long authorId;
    private String content;
    private int likesCount;
    private LocalDateTime createdAt;
}
