package faang.school.postservice.controller;

/*
создать два метода:
один для получения всех лайкнувших пользователей по id поста,
а второй — для получения всех лайкнувших пользователей по id коммента.
 */

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.service.like.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/likes")
@RequiredArgsConstructor
public class LikeController {
    private final LikeService likeService;

    @GetMapping("/post/{postId}")
    public List<UserDto> getLikesByPostId(@PathVariable Long postId) {
        return likeService.getUserLikedPost(postId);
    }
}
