package faang.school.postservice.controller.feed;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.feed.FeedResponseDto;
import faang.school.postservice.service.news_feed.FeedService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/feed")
@RestController
public class FeedController {
    private final FeedService feedService;
    private final UserContext userContext;

    @GetMapping
    public ResponseEntity<List<FeedResponseDto>> getFeed(
        @RequestParam(name = "after", required = false) Long afterPostId
    ) {
        Long userId = userContext.getUserId();
        List<FeedResponseDto> feed = feedService.getUserFeed(userId, afterPostId); // Запрашиваем фид
        return ResponseEntity.ok(feed);
    }
}

