package faang.school.postservice.model.redis;

import jakarta.persistence.Id;
import java.io.Serializable;
import java.util.LinkedHashSet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Version;
import org.springframework.data.redis.core.RedisHash;

@Builder
@Data
@RedisHash("Feed")
@AllArgsConstructor
@NoArgsConstructor
public class FeedCache implements Serializable {

  @Id
  private Long id;

  private LinkedHashSet<Long> postsIds;

  @Version
  private int version;

  public void addPost(Long postId) {
    postsIds.add(postId);
    version++;
  }

}
