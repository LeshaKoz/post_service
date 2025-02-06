package faang.school.postservice.controller;

import faang.school.postservice.dto.Post.CreatePostDraftDto;
import faang.school.postservice.dto.Post.PostResponseDto;
import faang.school.postservice.dto.Post.UpdatePostDto;
import faang.school.postservice.service.PostService;
import lombok.AllArgsConstructor;
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
@RequestMapping("/post")
@AllArgsConstructor
public class PostController {
    private final PostService postService;

    @PostMapping
    public PostResponseDto createDraft(@RequestBody CreatePostDraftDto postDraftDto) {
        return postService.createDraft(postDraftDto);
    }

    @PostMapping("/{id}")
    public PostResponseDto publishPost(@PathVariable long id) {
        return postService.publishPost(id);
    }

    @PutMapping
    public PostResponseDto updatePost(@RequestBody UpdatePostDto postDto) {
        return postService.updatePost(postDto);
    }

    @DeleteMapping("/{id}")
    public PostResponseDto safeDeletePost(@PathVariable long id) {
        return postService.safeDeletePost(id);
    }

    @GetMapping("/{id}")
    public PostResponseDto getPost(@PathVariable long id) {
        return postService.getPost(id);
    }

    @GetMapping("/user-drafts/{user-id}")
    public List<PostResponseDto> getUserDrafts(@PathVariable(name = "user-id") long userId) {
        return postService.getUserDrafts(userId);
    }

    @GetMapping("/project-drafts/{project-id}")
    public List<PostResponseDto> getProjectDrafts(@PathVariable(name = "project-id") long projectId) {
        return postService.getProjectDrafts(projectId);
    }

    @GetMapping("/user-posts/{user-id}")
    public List<PostResponseDto> getUserPost(@PathVariable(name = "user-id") long userId) {
        return postService.getUserPosts(userId);
    }

    @GetMapping("/project-posts/{project-id}")
    public List<PostResponseDto> getProjectPost(@PathVariable(name = "project-id") long projectId) {
        return postService.getProjectPosts(projectId);
    }
}
