package faang.school.postservice.controller.like;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.service.LikeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/likes")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/post/{postId}")
    public ResponseEntity<Void> likePost(@PathVariable Long postId, @Valid @RequestBody LikeDto likeDto) {
        likeService.likePost(postId, likeDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/post/{postId}")
    public ResponseEntity<Void> unlikePost(@PathVariable Long postId, @Valid @RequestBody LikeDto likeDto) {
        likeService.unlikePost(postId, likeDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/comment/{commentId}")
    public ResponseEntity<Void> likeComment(@PathVariable Long commentId, @Valid @RequestBody LikeDto likeDto) {
        likeService.likeComment(commentId, likeDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/comment/{commentId}")
    public ResponseEntity<Void> unlikeComment(@PathVariable Long commentId, @Valid @RequestBody LikeDto likeDto) {
        likeService.unlikeComment(commentId, likeDto);
        return ResponseEntity.ok().build();
    }
}