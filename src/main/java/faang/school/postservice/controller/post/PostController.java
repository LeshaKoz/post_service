package faang.school.postservice.controller.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.PostDtoValidationException;
import faang.school.postservice.service.post.interfaces.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/post-service/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostDto> createPostDraft(@RequestBody PostDto postDto) {
        if (postDto.getContent() == null || postDto.getContent().isBlank()) {
            throw new PostDtoValidationException(
                    "The content of the post must not be empty");
        }
        PostDto responseBody = postService.createPostDraft(postDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseBody);
    }

    @PostMapping("/publish")
    public ResponseEntity<PostDto> publishPost(@RequestBody PostDto postDto) {
        validateId(postDto.getId());
        return ResponseEntity.ok().body(postService.publishPost(postDto));
    }

    @PutMapping
    public ResponseEntity<PostDto> updatePost(@RequestBody PostDto postDto) {
        validateId(postDto.getId());
        if (postDto.getContent() == null || postDto.getContent().isBlank()) {
            throw new PostDtoValidationException(
                    "The content of the post must not be empty");
        }
        return ResponseEntity.ok().body(postService.updatePost(postDto));
    }

    @PostMapping("/delete")
    public ResponseEntity<PostDto> deletePost(@RequestBody PostDto postDto) {
        validateId(postDto.getId());
        return ResponseEntity.ok().body(postService.deletePost(postDto));
    }

    @PostMapping("/get")
    public ResponseEntity<PostDto> getPost(@RequestBody PostDto postDto) {
        validateId(postDto.getId());
        return ResponseEntity.ok().body(postService.getPost(postDto));
    }

    @PostMapping("/author/drafts")
    public ResponseEntity<List<PostDto>> getAuthorPostDrafts(@RequestBody PostDto postDto) {
        validateId(postDto.getAuthorId());
        return ResponseEntity.ok().body(postService.getAuthorPostDrafts(postDto));
    }

    @PostMapping("/project/drafts")
    public ResponseEntity<List<PostDto>> getProjectPostDrafts(@RequestBody PostDto postDto) {
        validateId(postDto.getProjectId());
        return ResponseEntity.ok().body(postService.getProjectPostDrafts(postDto));
    }

    @PostMapping("/author/published")
    public ResponseEntity<List<PostDto>> getAuthorPosts(@RequestBody PostDto postDto) {
        validateId(postDto.getAuthorId());
        return ResponseEntity.ok().body(postService.getAuthorPublishedPosts(postDto));
    }

    @PostMapping("/project/published")
    public ResponseEntity<List<PostDto>> getProjectPosts(@RequestBody PostDto postDto) {
        validateId(postDto.getProjectId());
        return ResponseEntity.ok().body(postService.getProjectPublishedPosts(postDto));
    }

    private void validateId(long id) {
        if (id < 1) {
            throw new PostDtoValidationException("ID must be greater than zero");
        }
    }
}
