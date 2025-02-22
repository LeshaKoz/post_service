package faang.school.postservice.model.redis;

import jakarta.persistence.Id;
import java.io.Serializable;
import java.util.LinkedHashSet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedCache implements Serializable {

  @Id
  private Long id;
  private LinkedHashSet<Long> postsIds;

}
