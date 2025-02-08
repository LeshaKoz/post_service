package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostCreateRequestDto;
import faang.school.postservice.dto.post.PostFilterDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.dto.post.PostUpdateRequestDto;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    @PostMapping("/")
    public PostResponseDto createPostDraft(@RequestBody PostCreateRequestDto postCreateRequestDto) {
        return postService.createPostDraft(postCreateRequestDto);
    }

    @PatchMapping("/{id}/publish")
    public PostResponseDto publishPostDraft(@PathVariable("id") Long postId) {
        return postService.publishPostDraft(postId);
    }

    @PutMapping("/{id}")
    public PostResponseDto updatePost(@PathVariable("id") Long postId, @RequestBody PostUpdateRequestDto postUpdateRequestDto) {
        return postService.updatePost(postId, postUpdateRequestDto);
    }

    @DeleteMapping("/{id}")
    public void deletePost(@PathVariable("id") Long postId) {
        postService.deletePost(postId);
    }

    @GetMapping("/{id}")
    public PostResponseDto getPost(@PathVariable("id") Long postId) {
        return postService.getPost(postId);
    }

    @GetMapping("/")
    public List<PostResponseDto> getFilteredPosts(@RequestParam Boolean isPublished,
                                             @RequestParam(required = false) Long projectId,
                                             @RequestParam(required = false) Long authorId) {

        PostFilterDto postFilter = PostFilterDto.builder()
                .authorId(authorId)
                .projectId(projectId)
                .isPublished(isPublished)
                .build();

        return postService.findAllByFilter(postFilter);
    }

}
