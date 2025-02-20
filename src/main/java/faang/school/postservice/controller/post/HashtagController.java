package faang.school.postservice.controller.post;

import faang.school.postservice.dto.post.HashtagRequestDto;
import faang.school.postservice.dto.post.HashtagResponseDto;
import faang.school.postservice.service.post.HashtagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Хэштеги")
@RestController
@RequiredArgsConstructor
@RequestMapping("${domain.path}/hashtags")
public class HashtagController {
    private final HashtagService hashtagService;

    @Operation(summary = "Получить все хэштеги")
    @GetMapping
    public List<HashtagResponseDto> getAllHashtags() {
        return hashtagService.getAllHashtags();
    }

    @Operation(summary = "Получить топ хэштегов")
    @GetMapping("/top")
    public List<HashtagResponseDto> getTopHashtags() {
        return hashtagService.getTopHashtags();
    }

    @Operation(summary = "Добавить хэштег к посту")
    @PostMapping
    public void addHashtagToPost(@RequestBody @Valid HashtagRequestDto hashtagRequestDto) {
        hashtagService.addHashtagToPost(hashtagRequestDto);
    }
}
