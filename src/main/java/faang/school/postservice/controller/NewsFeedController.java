package faang.school.postservice.controller;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.feed.PostFeedReadDto;
import faang.school.postservice.service.feed.NewsFeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/feed")
public class NewsFeedController {

    private final NewsFeedService newsFeedService;
    private final UserContext userContext;

    @GetMapping
    public List<PostFeedReadDto> getFeed(@RequestParam(value = "postId", required = false) Long postId) {
        long userId = userContext.getUserId();
        return newsFeedService.getFeed(userId, postId);
    }
}
