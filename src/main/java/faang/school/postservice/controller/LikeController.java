package faang.school.postservice.controller;

import faang.school.postservice.dto.like.CommentLikeDto;
import faang.school.postservice.dto.like.PostLikeDto;
import faang.school.postservice.service.LikeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/likes")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/post")
    public ResponseEntity<Void> likePost(@Valid @RequestBody PostLikeDto postLikeDto) {
        likeService.likePost(postLikeDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/post")
    public ResponseEntity<Void> unlikePost(@Valid @RequestBody PostLikeDto postLikeDto) {
        likeService.unlikePost(postLikeDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/comment")
    public ResponseEntity<Void> likeComment(@Valid @RequestBody CommentLikeDto commentLikeDto) {
        likeService.likeComment(commentLikeDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/comment")
    public ResponseEntity<Void> unlikeComment(@Valid @RequestBody CommentLikeDto commentLikeDto) {
        likeService.unlikeComment(commentLikeDto);
        return ResponseEntity.ok().build();
    }
}