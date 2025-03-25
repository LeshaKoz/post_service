package faang.school.postservice.controller.post;

import faang.school.postservice.dto.post.PostRequestDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.service.post.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Посты")
@RestController
@RequiredArgsConstructor
@RequestMapping("${domain.path}/posts")
@Validated
public class PostController {

    private final PostService postService;

    @Operation
    @GetMapping("/{postId}")
    public PostResponseDto getPostById(@PathVariable long postId) {
        return postService.getPostById(postId);
    }

    @Operation(summary = "Создать пост")
    @PostMapping
    public PostResponseDto createPost(@RequestBody @Valid PostRequestDto dto) {
        return postService.createPost(dto);
    }

    @Operation(summary = "Опубликовать пост")
    @PutMapping("/publish/{postId}")
    public PostResponseDto publishPost(@PathVariable @NotNull @Min(1) long postId) {
        return postService.publishPost(postId);
    }

    @PutMapping
    @Operation(summary = "Получить посты по хэштегу")
    @GetMapping("{hashtag}")
    public List<PostResponseDto> getPostsByHashtag(@PathVariable("hashtag") @NotBlank String hashtag) {
        return postService.getPostsByHashtag(hashtag);
    }
}
