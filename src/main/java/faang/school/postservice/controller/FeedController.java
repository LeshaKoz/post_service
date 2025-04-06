package faang.school.postservice.controller;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.PostReadDto;
import faang.school.postservice.service.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/posts")
public class FeedController {

    private final FeedService feedService;
    private final UserContext userContext;

    @GetMapping("/feed")
    public List<PostReadDto> getUserFeed(
            @RequestParam(value = "postId", required = false) Long postId) {

        long userId = userContext.getUserId();
        return feedService.getUserFeed(userId, postId);
    }

    @GetMapping("/heat")
    public void initFeedHeater() {
        feedService.initFeedHeater();
    }
}
