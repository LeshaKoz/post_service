package faang.school.postservice.controller;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Like;
import faang.school.postservice.service.LikeService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/like")
@Slf4j
public class LikeController {
    private LikeService likeServiceV2;
    private LikeMapper likeMapper;
    private PostMapper postMapper;
    private UserContext userContext;

    private static final String POST_NEGATIVE_ID = "postId is negative";

    @PostMapping("/post/{postId}/like")
    @ResponseBody
    public LikeDto likePost(@Validated @RequestBody LikeDto likeDto) {
//        if (likeDtoIsValidForPost(likeDto)) {
            likeDto.setUserId(userContext.getUserId());
            Like like = likeServiceV2.likePost(likeMapper.toEntity(likeDto));
            return likeMapper.toDto(like);
//        } else {
//            throw new DataValidationException(POST_NEGATIVE_ID);
//        }
    }

    @PostMapping("/post/{postId}/dislike")
    @ResponseBody
    public LikeDto dislikePost(@RequestBody LikeDto likeDto) {
        if (likeDtoIsValidForPost(likeDto)) {
            Like like = likeServiceV2.dislikePost(likeMapper.toEntity(likeDto));
            return likeMapper.toDto(like);
        } else {
            throw new DataValidationException(POST_NEGATIVE_ID);
        }
    }

    @PostMapping("/post/{postId}/comment/{commentId}/like")
    @ResponseBody
    public LikeDto likeComment(@RequestBody LikeDto likeDto) {
        if (likeDtoIsValidForComment(likeDto)) {
            Like like = likeServiceV2.likeComment(likeMapper.toEntity(likeDto));
            return likeMapper.toDto(like);
        } else {
            throw new DataValidationException(POST_NEGATIVE_ID);
        }
    }

    @PostMapping("/post/{postId}/comment/{commentId}/dislike")
    @ResponseBody
    public LikeDto dislikeComment(@RequestBody LikeDto likeDto) {
        if (likeDtoIsValidForComment(likeDto)) {
            Like like = likeServiceV2.dislikeComment(likeMapper.toEntity(likeDto));
            return likeMapper.toDto(like);
        } else {
            throw new DataValidationException(POST_NEGATIVE_ID);
        }
    }

    @GetMapping("/post/{postId}/likes")
    @ResponseBody
    public PostDto getNumberOfPostLikes(@PathVariable Long postId) {
        if (idIsValid(postId)) {
            return postMapper.toDto(likeServiceV2.getNumberOfPostLikes(postId));

        } else {
            throw new DataValidationException(POST_NEGATIVE_ID);
        }
    }

    private boolean likeDtoIsValidForPost(LikeDto likeDto) {
        return likeDto.getPostId() >= 0;
    }

    private boolean likeDtoIsValidForComment(LikeDto likeDto) {
        return likeDtoIsValidForPost(likeDto)
                && (likeDto.getCommentId() >= 0);
    }

    private boolean idIsValid(Long id) {
        return id >= 0;
    }
}
