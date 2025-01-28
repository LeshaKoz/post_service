package faang.school.postservice.controller.like;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.mapper.like.LikeMapper;
import faang.school.postservice.model.Like;
import faang.school.postservice.service.like.LikeService;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/like")
@RequiredArgsConstructor
@Validated
public class LikeController {
    private final LikeService likeService;
    private final LikeMapper likeMapper;

    @PostMapping("/post/{postId}")
    @ResponseStatus(HttpStatus.OK)
    public LikeDto createLikePost(@PathVariable @Positive Long postId, @RequestParam Long userId) {
        Like like = likeService.saveLikePost(postId, userId);
        return likeMapper.toDto(like);
    }

    @PostMapping("/comment/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public LikeDto createLikeComment(@PathVariable @Positive Long commentId,
                                     @RequestParam Long userId) {
        Like like = likeService.saveLikeComment(commentId, userId);
        return likeMapper.toDto(like);
    }

    @DeleteMapping("/post/{postId}")
    public void deleteLikePost(@PathVariable @Positive Long postId, @RequestParam Long userId) {
        likeService.deleteLikePost(postId, userId);
    }

    @DeleteMapping("/comment/{commentId}")
    public void deleteLikeComment(@PathVariable @Positive Long commentId, @RequestParam Long userId) {
        likeService.deleteLikeComment(commentId, userId);
    }

    @GetMapping("/postCountLikes/{postId}")
    public Long getCountLikesPost(@PathVariable @Positive Long postId) {
        return likeService.countLikesPost(postId);
    }

    @GetMapping("/post/{postId}")
    public List<String> likePost(@PathVariable @Positive Long postId) {
        return likeService.likesPost(postId);
    }

    @GetMapping("/comment/{commentId}")
    public List<String> likeComment(@PathVariable @Positive Long commentId) {
        return likeService.likesComment(commentId);
    }
}


