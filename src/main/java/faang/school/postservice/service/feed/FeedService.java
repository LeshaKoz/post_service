package faang.school.postservice.service.feed;

import faang.school.postservice.dto.event.PostEventDto;
import faang.school.postservice.model.redis.FeedCache;

public interface FeedService {

  void processPostEvent(PostEventDto dto);

  void updateUserFeed(Long userId, Long postId);

  FeedCache getUserFeed(Long userId);

  FeedCache getUserFeed(Long userId, int previousPostId, int pageSize);
}
