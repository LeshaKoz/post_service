package faang.school.postservice.controller;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.PostForNewsFeedDto;
import faang.school.postservice.service.NewsFeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/feed")
public class NewsFeedV1Controller {

    private final UserContext userContext;
    private final NewsFeedService newsFeedService;
    private final FeedHeaterService heaterService;

    @GetMapping
    public List<PostForNewsFeedDto> getFeed(@RequestParam(name = "postId", required = false) Long postId) {
        long userId = userContext.getUserId();
        return newsFeedService.getFeed(userId, postId);
    }

    @PostMapping("/start")
    public void heatFeedCache() {
        heaterService.heat();
    }
}
