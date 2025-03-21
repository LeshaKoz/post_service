package faang.school.postservice.controller;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/likes")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @GetMapping("/posts/{postId}")
    public ResponseEntity<List<UserDto>> getAllUsersWhoLikedPost(
            @PathVariable Long postId,
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(likeService.getAllUsersWhoLikedPost(postId));
    }

    @GetMapping("/comments/{commentsId}")
    public ResponseEntity<List<UserDto>> getAllUsersWhoLikedComment(
            @PathVariable Long commentsId,
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(likeService.getAllUsersWhoLikedComment(commentsId));
    }
}
