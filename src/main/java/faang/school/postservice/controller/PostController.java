package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.service.PostService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("${domain.base-path}/post")
public class PostController {
    private final PostService postService;

    @PostMapping(value = "/create-draft")
    public PostDto createDraft(@Valid @RequestBody PostDto postDto) {
        return postService.createDraft(postDto);
    }

    @PutMapping(value = "/publish/{postId}")
    public PostDto publish(@PathVariable @NotNull(message = "postId can't be null") Long postId) {
        return postService.publish(postId);
    }

    @PatchMapping(value = "/update")
    public PostDto update(@RequestParam Long id, @RequestParam String content) {
        return postService.update(id, content);
    }

    @PatchMapping(value = "/soft-delete/{id}")
    public PostDto softDelete(@PathVariable Long id) {
        return postService.softDelete(id);
    }

    @GetMapping(value = "/{id}")
    public PostDto getById(@PathVariable Long id) {
        return postService.getById(id);
    }

    @GetMapping(value = "/drafts-by-user/{userId}")
    public List<PostDto> getNotDeletedDraftByUserId(@PathVariable Long userId) {
        return postService.getNotDeletedDraftsByUserId(userId);
    }

    @GetMapping(value = "/drafts-by-project/{projectId}")
    public List<PostDto> getNotDeletedDraftsByProjectId(@PathVariable Long projectId) {
        return postService.getNotDeletedDraftsByProjectId(projectId);
    }

    @GetMapping(value = "/published-by-user/{userId}")
    public List<PostDto> getNotDeletedPublishedPostsByUserId(@PathVariable Long userId) {
        return postService.getNotDeletedPublishedPostsByUserId(userId);
    }

    @GetMapping(value = "/published-by-project/{projectId}")
    public List<PostDto> getNotDeletedPublishedPostsByProjectId(@PathVariable Long projectId) {
        return postService.getNotDeletedPublishedPostsByProjectId(projectId);
    }
}
