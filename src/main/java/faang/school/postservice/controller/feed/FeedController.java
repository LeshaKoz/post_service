package faang.school.postservice.controller.feed;

import faang.school.postservice.model.redis.FeedCache;
import faang.school.postservice.service.feed.FeedService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/feed")
public class FeedController {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  private final FeedService feedService;

  @GetMapping
  public FeedCache getFeed(@RequestParam int previousPostId, @RequestHeader("x-user-id") long userId) {
    return feedService.getUserFeed(userId, previousPostId, 20);
  }

}
