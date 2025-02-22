package faang.school.postservice.controller.feed;

import faang.school.postservice.dto.feed.FeedDto;
import faang.school.postservice.service.feed.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/feed")
public class FeedController {

  @Value("${newsfeed.posts.page-size}")
  private int pageSize;

  private final FeedService feedService;

  @GetMapping()
  public FeedDto getFeedDto(@RequestParam(required = false) Long previousPostId,
      @RequestHeader("x-user-id") Long userId) {
    return feedService.getFeed(userId, previousPostId, pageSize);
  }

}
