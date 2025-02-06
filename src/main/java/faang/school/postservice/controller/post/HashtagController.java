package faang.school.postservice.controller.post;

import faang.school.postservice.dto.post.HashtagRequestDto;
import faang.school.postservice.dto.post.HashtagResponseDto;
import faang.school.postservice.service.post.HashtagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${domain.path}/hashtags")
public class HashtagController {
    private final HashtagService hashtagService;

    @GetMapping
    public List<HashtagResponseDto> getAllHashtags() {
        return hashtagService.getAllHashtags();
    }

    @GetMapping("/top")
    public List<HashtagResponseDto> getTopHashtags() {
        return hashtagService.getTopHashtags();
    }

    @PostMapping
    public void addHashtagToPost(@RequestBody @Valid HashtagRequestDto hashtagRequestDto) {
        hashtagService.addHashtagToPost(hashtagRequestDto);
    }
}
