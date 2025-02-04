package faang.school.postservice.controller;

import faang.school.postservice.dto.like.comment.LikeCommentDto;
import faang.school.postservice.dto.like.comment.LikeCommentDtoResponse;
import faang.school.postservice.dto.like.post.LikePostDto;
import faang.school.postservice.dto.like.post.LikePostDtoResponse;
import faang.school.postservice.service.like.LikeService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/likes")
public class LikeController {
    
    private final LikeService likeService;

    @PostMapping("/post")
    @ResponseStatus(HttpStatus.CREATED)
    public LikePostDtoResponse likeForPost(@RequestBody @Validated({LikePostDto.Before.class,
            LikePostDto.After.class}) LikePostDto likePostDto) {

        return likeService.createLikeForPost(likePostDto);
    }

    @PostMapping("/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public LikeCommentDtoResponse likeForComment(@RequestBody @Validated({LikeCommentDto.Before.class,
            LikeCommentDto.After.class}) LikeCommentDto likeCommentDto) {

        return likeService.createLikeForComment(likeCommentDto);
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