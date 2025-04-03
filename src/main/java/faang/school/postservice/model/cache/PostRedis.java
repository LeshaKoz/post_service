package faang.school.postservice.model.cache;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;

@RedisHash("post")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PostRedis {
    @Id
    private long id;
    private long authorId;
    private String content;
    private long viewsCount;
    private LocalDateTime createdAt;
}
