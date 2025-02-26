package faang.school.postservice.controller;

import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.service.LikeService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/like")
@AllArgsConstructor
@Slf4j
public class LikeController {

    private LikeService likeService;

    private static final String POST_NEGATIVE_ID = "postId is negative";

    @PostMapping("/post/{postId}")
    @ResponseBody
    public void likePost(@RequestParam boolean like, @PathVariable Long postId ) {
        if (idIsValid(postId)) {
            likeService.likePost(like, postId);
        } else {
            throw new DataValidationException(POST_NEGATIVE_ID);
        }
    }

    @PostMapping("/post/{postId}/comment/{commentId}")
    @ResponseBody
    public void likeComment(@RequestParam boolean like, @PathVariable long postId, @PathVariable  long commentId) {
        if (idIsValid(postId) && idIsValid(commentId)) {
            likeService.likeComment(like, commentId, postId);
        } else {
            throw new DataValidationException(POST_NEGATIVE_ID);
        }
    }

    private boolean idIsValid(Long id) {
        return id >= 0;
    }
}
