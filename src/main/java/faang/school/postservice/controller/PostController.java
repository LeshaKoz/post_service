package faang.school.postservice.controller;

import faang.school.postservice.dto.PostDto;
import faang.school.postservice.service.PostService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;

    @PostMapping()
    public PostDto createPost(@NotNull @RequestBody PostDto postDto) {
        return postService.create(postDto);
    }

    @PutMapping("/publish/{postId}")
    public PostDto publish(@NotNull @PathVariable Long postId) {
        return postService.publish(postId);
    }

    @GetMapping("/{postId}")
    public PostDto getPost(@NotNull @PathVariable Long postId) {
        return postService.getPost(postId);
    }

    @PutMapping("/{postId}")
    public PostDto updatePost(@NotNull @PathVariable Long postId, @NotNull @RequestBody PostDto postDto) {
        return postService.update(postDto, postId);
    }

    @DeleteMapping("/{postId}")
    public void deletePost(@NotNull @PathVariable Long postId) {
        postService.deleteById(postId);
    }

    @GetMapping("/drafts/author/{authorId}")
    public List<PostDto> getDraftsByAuthor(@NotNull @PathVariable Long authorId) {
        return postService.findDraftsByAuthorId(authorId);
    }

    @GetMapping("/drafts/project/{projectId}")
    public List<PostDto> getDraftsByProject(@NotNull @PathVariable Long projectId) {
        return postService.findDraftsByProjectId(projectId);
    }

    @GetMapping("/published/author/{authorId}")
    public List<PostDto> getPublishedByAuthor(@NotNull @PathVariable Long authorId) {
        return postService.findPublishedByAuthorId(authorId);
    }

    @GetMapping("/published/project/{projectId}")
    public List<PostDto> getPublishedByProject(@NotNull @PathVariable Long projectId) {
        return postService.findPublishedByProjectId(projectId);
    }
}
