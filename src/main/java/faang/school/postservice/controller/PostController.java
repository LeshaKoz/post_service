package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostRequestDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.service.PostService;
import io.micrometer.common.util.StringUtils;
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

    @PutMapping("/create")
    PostResponseDto createPostDraft(PostRequestDto postRequestDto) {
        validateDto(postRequestDto);
        return postService.createPostDraft(postRequestDto);
    }

    @PostMapping("/publish")
    PostResponseDto publishPostDraft(Long postId) {
        validatePostId(postId);
        return postService.publishPostDraft(postId);
    }

    @PatchMapping("/update")
    PostResponseDto updatePost(PostRequestDto postRequestDto) {
        validateDto(postRequestDto);
        return postService.updatePost(postRequestDto);
    }

    @DeleteMapping("/delete/{Id}")
    void deletePost(Long postId) {
        validatePostId(postId);
        postService.deletePost(postId);
    }

    @GetMapping("/{Id}")
    PostResponseDto getPost(Long postId) {
        validatePostId(postId);
        return postService.getPost(postId);
    }

    @GetMapping("/draftByProject/{Id}")
    List<PostResponseDto> getProjectPostDrafts(Long projectId) {
        validateProjectId(projectId);
        return postService.getProjectPostDrafts(projectId);
    }

    @GetMapping("/draftByUser/{Id}")
    List<PostResponseDto> getUserPostDrafts(Long userId) {
        validateUserId(userId);
        return postService.getUserPostDrafts(userId);
    }

    @GetMapping("/byProject/{Id}")
    List<PostResponseDto> getProjectPosts(Long projectId) {
        validateProjectId(projectId);
        return postService.getProjectPosts(projectId);
    }

    @GetMapping("/byUser/{Id}")
    List<PostResponseDto> getUserPosts(Long userId) {
        validateUserId(userId);
        return postService.getProjectPosts(userId);
    }

    private void validatePostId(Long postId) {
        validateEntityId("Post", postId);
    }

    private void validateUserId(Long userId) {
        validateEntityId("User", userId);
    }

    private void validateProjectId(Long projectId) {
        validateEntityId("Project", projectId);
    }

    private void validateEntityId(String checkedEntity, Long id) {
        if (null == id || id < 1) {
            log.error("{} id is incorrect or empty : {}", checkedEntity, id);
            throw new IllegalArgumentException(checkedEntity + " id is incorrect or empty");
        }
    }

    private void validateDto(PostRequestDto postRequestDto) {
        if (null == postRequestDto.id() || postRequestDto.id() < 1) {
            log.error("Incorrect Post request DTO, empty id. DTO : {}", postRequestDto);
            throw new IllegalArgumentException("Incorrect Request DTO, empty id");
        }

        if (StringUtils.isBlank(postRequestDto.content())) {
            log.error("Incorrect Post request DTO, empty content. DTO : {}", postRequestDto);
            throw new IllegalArgumentException("Incorrect Post request DTO, empty content");
        }
    }
}
