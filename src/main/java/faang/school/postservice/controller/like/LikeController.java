package faang.school.postservice.controller.like;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.service.like.LikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/likes")
public class LikeController {

    private final LikeService likeService;

    @GetMapping("/posts/{postId}")
    public List<UserDto> getUsersWhoLikedPost(@PathVariable Long postId) {
        log.info("Received request to get users who liked post with id: {}", postId);
        List<UserDto> users = likeService.getUsersWhoLikedPost(postId);
        log.info("Returned {} users who liked post with id: {}", users.size(), postId);
        return users;
    }

    @GetMapping("/comments/{commentId}")
    public List<UserDto> getUsersWhoLikedComment(@PathVariable Long commentId) {
        log.info("Received request to get users who liked comment with id: {}", commentId);
        List<UserDto> users = likeService.getUsersWhoLikedComment(commentId);
        log.info("Returned {} users who liked comment with id: {}", users.size(), commentId);
        return users;
    }
}