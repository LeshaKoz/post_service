package faang.school.postservice.controller;

import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static faang.school.postservice.utils.ValidationUtils.validateLike;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/")
public class LikeController {

    private final LikeService likeService;

    @PostMapping("like/post")
    public PostDto addLikeToPost(@RequestBody LikeDto likeDto) {
        validateLike(likeDto);
        return likeService.addLikeToPost(likeDto);
    }

    @DeleteMapping("/like/post/{postId}/user/{userId}")
    public PostDto removeLikeFromPost(@RequestParam Long postId, @RequestParam Long userId) {
        LikeDto likeDto = new LikeDto(userId,postId,null);
        validateLike(likeDto);
        return likeService.removeLikeFromPost(likeDto);
    }

    @PostMapping("/like/comment")
    public CommentDto addLikeToComment(@RequestBody LikeDto likeDto) {
        validateLike(likeDto);
        return likeService.addLikeToComment(likeDto);
    }

    @DeleteMapping("/like/comment/{commentId}/user/{userId}")
    public CommentDto removeLikeFromComment(@RequestParam Long commentId, @RequestParam Long userId) {
        LikeDto likeDto = new LikeDto(userId,null, commentId);
        validateLike(likeDto);
        return likeService.removeLikeFromComment(likeDto);
    }
}
