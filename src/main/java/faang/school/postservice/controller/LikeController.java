package faang.school.postservice.controller;

import faang.school.postservice.service.like.LikeService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/likes")
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/post/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public void likeForPost(@PathVariable("id") Long postId) {
        likeService.createLikeForPost(postId);
    }

    @PostMapping("/comment/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public void likeForComment(@PathVariable("id") Long commentId) {
        likeService.createLikeForComment(commentId);
    }

    @DeleteMapping("/post/{postId}")
    public void deleteLikeFromPost(@PathVariable @Positive Long postId) {
        likeService.deleteLikeFromPost(postId);
    }

    @DeleteMapping("/comment/{commentId}")
    public void deleteLikeFromComment(@PathVariable @Positive Long commentId) {
        likeService.deleteLikeFromComment(commentId);
    }
}