package faang.school.postservice.model.redis;

import jakarta.persistence.Id;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@RedisHash(value = "Posts", timeToLive = 90_000)
public class PostCache implements Serializable {

  @Id
  private Long id;
  private String content;
  private Long authorId;
  private LocalDateTime updatedAt;
  private String authorName;

}