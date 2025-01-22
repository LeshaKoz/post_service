package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostRequestDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static faang.school.postservice.utils.Constants.API_VERSION_1;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(API_VERSION_1 + "/post")
public class PostController {
    private final PostService postService;
    private final PostControllerValidator postControllerValidator;

    @PutMapping("/create")
    PostResponseDto createPostDraft(PostRequestDto postRequestDto) {
        log.info("Create post draft: {}", postRequestDto);
        postControllerValidator.validateDto(postRequestDto);
        return postService.createPostDraft(postRequestDto);
    }

    @PostMapping("/publish")
    PostResponseDto publishPostDraft(Long postId) {
        log.info("Publish post id {}", postId);
        postControllerValidator.validatePostId(postId);
        return postService.publishPostDraft(postId);
    }

    @PatchMapping("/update")
    PostResponseDto updatePost(PostRequestDto postRequestDto) {
        log.info("Update post: {}", postRequestDto);
        postControllerValidator.validateDto(postRequestDto);
        return postService.updatePost(postRequestDto);
    }

    @DeleteMapping("/delete/{Id}")
    void deletePost(Long postId) {
        log.info("Delete post id {}", postId);
        postControllerValidator.validatePostId(postId);
        postService.deletePost(postId);
    }

    @GetMapping("/{Id}")
    PostResponseDto getPost(Long postId) {
        postControllerValidator.validatePostId(postId);
        return postService.getPost(postId);
    }

    @GetMapping("/draftByProject/{Id}")
    List<PostResponseDto> getProjectPostDrafts(Long projectId) {
        postControllerValidator.validateProjectId(projectId);
        return postService.getProjectPostDrafts(projectId);
    }

    @GetMapping("/draftByUser/{Id}")
    List<PostResponseDto> getUserPostDrafts(Long userId) {
        postControllerValidator.validateUserId(userId);
        return postService.getUserPostDrafts(userId);
    }

    @GetMapping("/byProject/{Id}")
    List<PostResponseDto> getProjectPosts(Long projectId) {
        postControllerValidator.validateProjectId(projectId);
        return postService.getProjectPosts(projectId);
    }

    @GetMapping("/byUser/{Id}")
    List<PostResponseDto> getUserPosts(Long userId) {
        postControllerValidator.validateUserId(userId);
        return postService.getUserPosts(userId);
    }
}
