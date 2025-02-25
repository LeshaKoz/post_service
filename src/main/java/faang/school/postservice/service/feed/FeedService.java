package faang.school.postservice.service.feed;

import faang.school.postservice.dto.event.PostEventDto;
import faang.school.postservice.dto.feed.FeedDto;

public interface FeedService {

  void processPostEvent(PostEventDto dto);

  void updateUserFeed(Long userId, PostEventDto dto);

  FeedDto getFeed(Long userId, Long previousPostId, int pageSize);

}
