package faang.school.postservice.controller;

import faang.school.postservice.dto.hashtag.HashtagDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.service.HashtagService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("api/hashtags")
@RestController
public class HashtagController {

    private final HashtagService hashtagService;

    @GetMapping("/trending")
    public List<HashtagDto> getTrendingHashtags() {
         return hashtagService.getTrendingHashtags();
    }

    @GetMapping("/{tag}")
    public List<PostDto> getPostsByHashtag(
            @NotNull(message = "Tag cannot be null") @PathVariable String tag,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return hashtagService.getPostsByHashtag(tag, page, size);
    }


    @GetMapping("/search")
    public List<HashtagDto> searchHashtags(
            @NotNull(message = "Query cannot be null") @RequestParam String q) {
        return hashtagService.searchHashtags(q);
    }
}
