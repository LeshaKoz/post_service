package faang.school.postservice.controller;

import faang.school.postservice.dto.like.LikeCommentRequest;
import faang.school.postservice.dto.like.LikePostRequest;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.service.LikeService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/likes")
@RequiredArgsConstructor
public class LikeController {
    private final LikeService service;

    private final LikeService likeService;

    @PostMapping("/posts/like")
    public ResponseEntity<?> toggleLikePost(@Valid @NotNull @RequestBody LikePostRequest request) {
        service.toggleLikePost(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/comments/like")
    public ResponseEntity<?> toggleLikeComment(@Valid @NotNull @RequestBody LikeCommentRequest request) {
        service.toggleLikeComment(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/post/{postId}")
    public List<UserDto> getUsersLikedToPost(@Valid @NotNull @PathVariable Long postId) {
        return likeService.getLikedUsersToPost(postId);
    }

    @GetMapping("/comment/{commentId}")
    public List<UserDto> getUsersLikedToComment(@Valid @NotNull @PathVariable Long commentId) {
        return likeService.getLikedUsersToComment(commentId);
    }

}

