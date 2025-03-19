package faang.school.postservice.controller;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/likes")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/post-{postId}")
    public void putLikeOnPost(@PathVariable Long postId, @RequestBody LikeDto likeDto) {
        likeService.putLikeOnPost(postId, likeDto);
    }

    @DeleteMapping("/post-{postId}")
    public void removeLikeAtPost(@PathVariable Long postId, @RequestBody LikeDto likeDto) {
        likeService.removeLikeAtPost(postId, likeDto);
    }

    @PostMapping("/comment-{commentId}")
    public void getLikeOnComment(@PathVariable Long commentId, @RequestBody LikeDto likeDto) {
        likeService.putLikeOnComment(commentId, likeDto);
    }

    @DeleteMapping("/comment-{commentId}")
    public void removeLikeAtComment(@PathVariable Long commentId, @RequestBody LikeDto likeDto) {
        likeService.removeLikeAtComment(commentId, likeDto);
    }
}
