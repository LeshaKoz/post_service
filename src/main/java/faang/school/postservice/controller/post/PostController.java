package faang.school.postservice.controller.post;

import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${domain.path}/posts")
public class PostController {
    private final PostService postService;

    @GetMapping("{hashtag}")
    public ResponseEntity<List<PostResponseDto>> getPostsByHashtag(@PathVariable("hashtag") String hashtag) {
        List<PostResponseDto> postsByHashtag = postService.getPostsByHashtag(hashtag);
        return new ResponseEntity<>(postsByHashtag, HttpStatus.OK);
    }
}
