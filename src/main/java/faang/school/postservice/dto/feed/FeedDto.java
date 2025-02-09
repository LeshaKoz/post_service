package faang.school.postservice.dto.feed;

import faang.school.postservice.model.redis.PostCache;
import faang.school.postservice.model.redis.UserCache;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedDto {

  private UserCache user;
  private List<PostCache> posts;

}
