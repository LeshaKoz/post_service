package faang.school.postservice.controller;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.service.like.LikeServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/likes")
@RequiredArgsConstructor
public class LikeController {
    private final LikeServiceImpl likeServiceImpl;

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<UserDto>> getLikesByPostId(@PathVariable Long postId) {
        try {
            List<UserDto> users = likeServiceImpl.getUserLikedPost(postId);
            if (users.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(users);
            }
            return ResponseEntity.ok(users);
        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @GetMapping("/comment/{commentId}")
    public ResponseEntity<List<UserDto>> getLikesByCommentId(@PathVariable Long commentId) {
        try {
            List<UserDto> users = likeServiceImpl.getUserLikedComment(commentId);
            if (users.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(users);
            }
            return ResponseEntity.ok(users);
        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
}
