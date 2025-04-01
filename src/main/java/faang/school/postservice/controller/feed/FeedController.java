package faang.school.postservice.controller.feed;

import faang.school.postservice.dto.feed.FeedResponse;
import faang.school.postservice.service.feed.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("${domain.path}/feeds")
@Validated
public class FeedController {

    private final FeedService feedService;

    @GetMapping("/user/{userId}")
    public FeedResponse getNewsFeed(@PathVariable long userId, @RequestParam(required = false) Long after) {
        return feedService.getNewsFeed(userId, after);
    }
}
