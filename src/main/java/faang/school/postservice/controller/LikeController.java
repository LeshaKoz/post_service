package faang.school.postservice.controller;

/*
создать два метода:
один для получения всех лайкнувших пользователей по id поста,
а второй — для получения всех лайкнувших пользователей по id коммента.
 */

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/getlike")
@RequiredArgsConstructor
public class LikeController {
    private final LikeService likeService;

    @GetMapping("/{authorId}")
    public List<UserDto> getLikesByPostId(@PathVariable Long postId) {
        return likeService.getLikesByPostId(postId);
    }
}
