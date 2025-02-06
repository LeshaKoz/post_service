package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostDTO;
import faang.school.postservice.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
@Validated
public class PostController {
    private final PostService postService;

    @PostMapping("/drafts")
    public PostDTO createDraft(@Valid @RequestBody PostDTO postDTO) {
        log.info("Endpoint <createDraft> called with URI='/api/v1/posts'");
        return postService.createDraft(postDTO);
    }

    @PostMapping("/{postId}")
    public PostDTO publishPost(@PathVariable long postId) {
        log.info("Endpoint <publishPost> called with URI='/api/v1/posts/publish/{postId}'");
        return postService.publishPost(postId);
    }

    @PutMapping
    public PostDTO updatePost(@RequestBody PostDTO postDTO) {
        log.info("Endpoint <updatePost> called with URI='/api/v1/posts'");
        return postService.updatePost(postDTO);
    }

    @PutMapping("/{postId}")
    public PostDTO deletePost(@PathVariable long postId) {
        log.info("Endpoint <deletePost> called with URI='/api/v1/posts/{postId}'");
        return postService.deletePost(postId);
    }

    @GetMapping("/{postId}")
    public PostDTO getPostById(@PathVariable long postId) {
        log.info("Endpoint <getPostById> called with URI='/api/v1/posts/{postId}'");
        return postService.getPostById(postId);
    }

    @GetMapping("/drafts/{authorId}")
    public List<PostDTO> getAllDraftsByAuthorId(@PathVariable long authorId) {
        log.info("Endpoint <getAllDraftsByAuthorId> called with URI='/api/v1/posts/drafts/{authorId}'");
        return postService.getAllDraftsByAuthorId(authorId);
    }

    @GetMapping("/drafts/{projectId}")
    public List<PostDTO> getAllDraftsByProjectId(@PathVariable long projectId) {
        log.info("Endpoint <getAllDraftsByProjectId> called with URI='/api/v1/posts/drafts/{projectId}'");
        return postService.getAllDraftsByProjectId(projectId);
    }

    @GetMapping("/{authorId}")
    public List<PostDTO> getAllPostsByAuthorId(@PathVariable long authorId) {
        log.info("Endpoint <getAllPostsByAuthorId> called with URI='/api/v1/posts/{authorId}'");
        return postService.getAllPostsByAuthorId(authorId);
    }

    @GetMapping("/{projectId}")
    public List<PostDTO> getAllPostsByProjectId(@PathVariable long projectId) {
        log.info("Endpoint <getAllPostsByProjectId> called with URI='/api/v1/posts/{projectId}'");
        return postService.getAllPostsByProjectId(projectId);
    }
}
