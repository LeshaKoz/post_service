package faang.school.postservice.controller.post;

import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.service.post.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Посты")
@RestController
@RequiredArgsConstructor
@RequestMapping("${domain.path}/posts")
public class PostController {

    private final PostService postService;

    @Operation(summary = "Получить посты по хэштегу")
    @GetMapping("{hashtag}")
    public ResponseEntity<List<PostResponseDto>> getPostsByHashtag(@PathVariable("hashtag") String hashtag) {
        List<PostResponseDto> postsByHashtag = postService.getPostsByHashtag(hashtag);
        return new ResponseEntity<>(postsByHashtag, HttpStatus.OK);
    }
}
